package com.barchart.feed.ddf.datalink.provider.pipeline;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;

import com.barchart.feed.ddf.datalink.provider.FeedClientDDF;

public class PipelineFactoryWebSocket implements ChannelPipelineFactory {

	private Executor exc;
	private FeedClientDDF feed;
	final HashMap<String, String> customHeaders = new HashMap<String, String>();
	final WebSocketClientHandshaker handshaker;
	private SimpleChannelHandler ddfHandler;

	//

	public PipelineFactoryWebSocket(Executor exc, FeedClientDDF feed, SimpleChannelHandler ddfHandler, URI host) {
		this.exc = exc;
		this.feed = feed;
		this.ddfHandler = ddfHandler;

		customHeaders.put("X-Application", "DDF-3");
		customHeaders.put("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");

		handshaker = new WebSocketClientHandshakerFactory().newHandshaker(host, WebSocketVersion.V13, null, true,
				customHeaders);
	}

	public WebSocketClientHandshaker getHandshaker() {
		return handshaker;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("decoder", new HttpResponseDecoder());
		pipeline.addLast("encoder", new HttpRequestEncoder());
		// websocket handler
		pipeline.addLast("ws-handler", new WebSocketClientHandler(handshaker, customHeaders));
		// now add message deframer
		pipeline.addLast("ddf-deframer", new MsgDeframerDDF());
		// now add message decoder
		pipeline.addLast("ddf-decoder", new MsgDecoderDDF());
		// outbound message encoded
		//pipeline.addLast("ddf-ws-encoder", new MessageEncoderWebSocketDDF());
		// feed orchestrator
		pipeline.addLast("ddf-feed", ddfHandler);

		return pipeline;
	}

}
