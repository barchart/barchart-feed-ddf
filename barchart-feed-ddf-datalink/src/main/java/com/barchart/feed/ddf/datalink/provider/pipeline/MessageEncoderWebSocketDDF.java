package com.barchart.feed.ddf.datalink.provider.pipeline;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class MessageEncoderWebSocketDDF extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {

		/*
		 * so the subscription handler sends strings, convert them here is
		 * necessary
		 */

		if (msg instanceof String) {
			return new TextWebSocketFrame(msg.toString());
		}

		return msg;
	}

}
