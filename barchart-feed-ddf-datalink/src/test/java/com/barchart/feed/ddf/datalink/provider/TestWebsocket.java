package com.barchart.feed.ddf.datalink.provider;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.provider.pipeline.MsgDecoderDDF;
import com.barchart.feed.ddf.datalink.provider.pipeline.MsgDeframerDDF;
import com.barchart.feed.ddf.datalink.provider.pipeline.WebSocketClientHandler;

public class TestWebsocket {

	static final Logger log = LoggerFactory.getLogger(TestWebsocket.class);

	private final URI uri;

	public TestWebsocket(URI uri) throws Exception {
		this.uri = uri;
	}

	public void run() throws Exception {
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		Channel ch = null;

		try {

			final HashMap<String, String> customHeaders = new HashMap<String, String>();
			customHeaders.put("X-Application", "Trader");
			customHeaders.put("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");

			// Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08
			// or V00.
			// If you change it to V00, ping is not supported and remember to
			// change
			// HttpResponseDecoder to WebSocketHttpResponseDecoder in the
			// pipeline.
			final WebSocketClientHandshaker handshaker = new WebSocketClientHandshakerFactory().newHandshaker(uri,
					WebSocketVersion.V13, null, true, customHeaders);

			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() throws Exception {
					ChannelPipeline pipeline = Channels.pipeline();

					pipeline.addLast("decoder", new HttpResponseDecoder());
					pipeline.addLast("encoder", new HttpRequestEncoder());
					// websocket handler
					pipeline.addLast("ws-handler", new WebSocketClientHandler(handshaker, customHeaders));
					// now add message deframer
					pipeline.addLast("ddf-deframer", new MsgDeframerDDF());
					// now add message decoder
					pipeline.addLast("ddf-decoded", new MsgDecoderDDF());
					return pipeline;
				}
			});

			// Connect
			log.debug("WebSocket Client connecting");

			ChannelFuture future = bootstrap.connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
			future.syncUninterruptibly();

			ch = future.getChannel();
			handshaker.handshake(ch).syncUninterruptibly();

			// send some login messages
			Thread.sleep(500);
			ch.write(new TextWebSocketFrame("LOGIN user:pass\n"));
			// Thread.sleep(1500);
			ch.write(new TextWebSocketFrame("VER 4\n"));
			// Thread.sleep(1500);
			ch.write(new TextWebSocketFrame("GO ESH8\n"));

			while (true) {
				Thread.sleep(500);
			}

		} finally {
			if (ch != null) {
				ch.close();
			}
			bootstrap.releaseExternalResources();
		}
	}

	public static void main(String[] args) throws Exception {
		URI uri;
		if (args.length > 0) {
			uri = new URI(args[0]);
		} else {
			uri = new URI("ws://qsws-us-e-02.aws.barchart.com:80/jerq");
		}
		new TestWebsocket(uri).run();
	}
}
