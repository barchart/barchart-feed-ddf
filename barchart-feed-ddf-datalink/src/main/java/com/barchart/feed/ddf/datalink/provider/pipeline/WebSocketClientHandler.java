package com.barchart.feed.ddf.datalink.provider.pipeline;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.zip.Inflater;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.provider.util.ByteAccumulator;

public class WebSocketClientHandler extends SimpleChannelUpstreamHandler {

	private final Logger log = LoggerFactory.getLogger(WebSocketClientHandler.class);

	private static final byte[] COMPRESSION_TERMINATOR = { (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF };
	private static final ByteBuffer TAIL_BYTES_BUF = ByteBuffer.wrap(COMPRESSION_TERMINATOR);
	private static final int INPUT_MAX_BUFFER_SIZE = 8 * 1024;

	private final Inflater inflater = new Inflater(true);

	private final WebSocketClientHandshaker handshaker;
	@SuppressWarnings("unused")
	private final Map<String, String> headers;

	public WebSocketClientHandler(WebSocketClientHandshaker handshaker, Map<String, String> headers) {
		this.handshaker = handshaker;
		this.headers = headers;
	}

	private boolean isCompressed(WebSocketFrame frame) {
		return frame.getRsv() > 0;
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.warn("WebSocket Client Disconnected");
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Channel ch = ctx.getChannel();

		if (!handshaker.isHandshakeComplete()) {
			handshaker.finishHandshake(ch, (HttpResponse) e.getMessage());
			log.debug("Websocket Client Connected");
			return;
		}

		if (e.getMessage() instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) e.getMessage();
			throw new Exception("Unexpected HttpResponse (status = " + response.getStatus() + ", content="
					+ response.getContent().toString(CharsetUtil.UTF_8) + ')');
		}

		final WebSocketFrame frame = (WebSocketFrame) e.getMessage();

		if (frame instanceof TextWebSocketFrame) {

			final TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;

			/* no compression, plain text */
			// log.warn("Text DDF: {}", textFrame.getText());

			/* send upstream to ddf deframer */
			ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), textFrame.getBinaryData(), null));

		} else if (frame instanceof BinaryWebSocketFrame) {

			final BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame) frame;
			final ChannelBuffer frameBuffer = binFrame.getBinaryData();

			byte[] array = frameBuffer.array();

			/* permessage-deflate */
			if (isCompressed(frame)) {
				final ByteAccumulator accumulator = new ByteAccumulator(INPUT_MAX_BUFFER_SIZE);
				decompress(accumulator, frameBuffer.toByteBuffer());
				array = accumulator.toByteBuffer().array();

				if (frame.isFinalFragment()) {
					decompress(accumulator, TAIL_BYTES_BUF.slice());
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("WebSocket Binary: isCompressed = {} Frame: = {}", isCompressed(frame), new String(array));
			}

			/* send upstream to ddf deframer */
			ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), ChannelBuffers.copiedBuffer(array), null));

		} else if (frame instanceof PongWebSocketFrame) {
			if (log.isDebugEnabled()) {
				log.debug("WebSocket Client received pong");
			}
		} else if (frame instanceof CloseWebSocketFrame) {
			if (log.isDebugEnabled()) {
				log.debug("WebSocket Client received closing");
			}
			ch.close();
		} else if (frame instanceof PingWebSocketFrame) {
			if (log.isDebugEnabled()) {
				log.debug("WebSocket Client received ping, response with pong");
			}
			ch.write(new PongWebSocketFrame(frame.getBinaryData()));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		final Throwable t = e.getCause();
		log.error("Exception in WebSocketClientHandler", t);
	}

	private void decompress(ByteAccumulator accumulator, ByteBuffer buf) throws Exception {
		if ((buf == null) || (!buf.hasRemaining())) {
			return;
		}
		byte[] output = new byte[INPUT_MAX_BUFFER_SIZE];

		while (buf.hasRemaining() && inflater.needsInput()) {
			if (!supplyInput(inflater, buf)) {
				if (log.isDebugEnabled()) {
					log.debug("Needed input, but no buffer could supply input");
				}
				return;
			}

			int read;

			while ((read = inflater.inflate(output)) >= 0) {
				if (read == 0) {
					if (log.isDebugEnabled()) {
						log.debug("Decompress: read 0 {}", inflater);
					}
					break;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Decompressed {} bytes: {}", read, inflater);
					}
					accumulator.copyChunk(output, 0, read);
				}
			}
		}

	}

	private boolean supplyInput(Inflater inflater, ByteBuffer buf) {
		if (buf.remaining() <= 0) {
			if (log.isDebugEnabled()) {
				log.debug("No data left left to supply to Inflater");
			}
			return false;
		}

		byte input[];
		int inputOffset;
		int len;

		if (buf.hasArray()) {
			// no need to create a new byte buffer, just return this one.
			len = buf.remaining();
			input = buf.array();
			inputOffset = buf.position() + buf.arrayOffset();
			buf.position(buf.position() + len);
		} else {
			// Only create an return byte buffer that is reasonable in size
			len = Math.min(INPUT_MAX_BUFFER_SIZE, buf.remaining());
			input = new byte[len];
			inputOffset = 0;
			buf.get(input, 0, len);
		}

		inflater.setInput(input, inputOffset, len);
		if (log.isDebugEnabled()) {
			log.debug("Supplied {} input bytes: {}", input.length);
		}
		return true;
	}
}
