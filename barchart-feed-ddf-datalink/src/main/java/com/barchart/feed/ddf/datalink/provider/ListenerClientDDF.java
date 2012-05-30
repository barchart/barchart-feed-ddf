/**
 * 
 */
package com.barchart.feed.ddf.datalink.provider;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.AdaptiveReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;

/**
 * @author g-litchfield
 * 
 */
public class ListenerClientDDF extends SimpleChannelHandler implements
		DDF_FeedClientBase {

	/** use slf4j for internal NETTY LoggingHandler facade */
	static {
		final InternalLoggerFactory defaultFactory = new Slf4JLoggerFactory();
		InternalLoggerFactory.setDefaultFactory(defaultFactory);
	}

	private static final Logger log = LoggerFactory
			.getLogger(ListenerClientDDF.class);

	private final ConnectionlessBootstrap boot;

	private DatagramChannel channel;

	private volatile DDF_MessageListener msgListener = null;

	private final Executor runner;

	private final int socketAddress;

	ListenerClientDDF(final int socketAddress, final Executor executor) {

		this.socketAddress = socketAddress;
		runner = executor;

		final DatagramChannelFactory channelFactory =
				new NioDatagramChannelFactory(runner);

		boot = new ConnectionlessBootstrap(channelFactory);

		final ChannelPipelineFactory pipelineFactory =
				new PipelineFactoryDDF(this);

		boot.setPipelineFactory(pipelineFactory);

		boot.setOption("broadcast", "false");
		boot.setOption("sendBufferSize", 1048576);
		boot.setOption("receiveBufferSize", 1048576);

		boot.setOption("receiveBufferSizePredictorFactory",
				new AdaptiveReceiveBufferSizePredictorFactory(1048576, 1048576,
						2097152));

	}

	private final BlockingQueue<DDF_BaseMessage> messageQueue =
			new LinkedBlockingQueue<DDF_BaseMessage>();

	private final RunnerDDF messageTask = new RunnerDDF() {

		@Override
		protected void runCore() {
			while (true) {
				try {
					final DDF_BaseMessage message = messageQueue.take();

					if (msgListener != null) {
						msgListener.handleMessage(message);
					}

				} catch (final InterruptedException e) {
					log.trace("terminated");
					return;
				} catch (final Throwable e) {
					log.error("message delivery failed", e);
				}
			}
		}

	};

	@Override
	public void startup() {

		runner.execute(messageTask);

		boot.bind(new InetSocketAddress(socketAddress));

	}

	@Override
	public void shutdown() {

		messageTask.interrupt();

		messageQueue.clear();

		if (channel != null) {
			channel.close();
			channel = null;
		}

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx,
			final ExceptionEvent e) throws Exception {
		log.warn("SimpleChannelHandler caught exception");

		// ctx.sendUpstream(e);
	}

	private void postMessage(final DDF_BaseMessage message) {
		try {
			messageQueue.put(message);
		} catch (final InterruptedException e) {
			log.trace("terminated");
		}
	}

	private void doMarket(final DDF_BaseMessage message) {

		postMessage(message);

	}

	private void doTimestamp(final DDF_BaseMessage message) {

		postMessage(message);

	}

	@Override
	public void messageReceived(final ChannelHandlerContext context,
			final MessageEvent eventIn) throws Exception {

		final Object messageIn = eventIn.getMessage();

		if (!(messageIn instanceof DDF_BaseMessage)) {
			context.sendUpstream(eventIn);
			return;
		}

		final DDF_BaseMessage message = (DDF_BaseMessage) messageIn;
		final DDF_MessageType type = message.getMessageType();

		if (type.isMarketMessage) {
			doMarket(message);
			return;
		}

		if (type.isControlTimestamp) {
			doTimestamp(message);
			return;
		}

		log.debug("unknown message : {}", message);

	}

	@Override
	public void bindMessageListener(final DDF_MessageListener msgListener) {
		this.msgListener = msgListener;
	}

}
