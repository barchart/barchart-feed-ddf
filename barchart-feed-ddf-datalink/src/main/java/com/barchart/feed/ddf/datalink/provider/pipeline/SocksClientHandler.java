/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider.pipeline;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.FeedEvent;
import com.barchart.feed.ddf.datalink.provider.DDF_SocksProxy;
import com.barchart.feed.ddf.datalink.provider.FeedClientDDF;
import com.barchart.feed.ddf.datalink.provider.SocksProxyException;


public class SocksClientHandler extends SimpleChannelHandler {

	 private enum Step {
		 METHOD_SELECTION,
		 AUTH,
		 CONNECT,
		 BIND;
	 };
	 
	 /// <summary>
     /// Authentication itemType.
     /// </summary>
	 private enum SocksAuthentication
     {
         /// <summary>
         /// No authentication used.
         /// </summary>
         None,
         /// <summary>
         /// Username and password authentication.
         /// </summary>
         UsernamePassword
     }
     
	private Step lastStep = null;
	    
	private byte SOCKS5_VERSION_NUMBER = 5;
	private byte SOCKS5_RESERVED = 0x00;

    private byte SOCKS5_AUTH_NUMBER_OF_AUTH_METHODS_SUPPORTED = 2;
    
	private byte SOCKS5_AUTH_METHOD_REPLY_NO_ACCEPTABLE_METHODS = -1;
    private byte SOCKS5_AUTH_METHOD_NO_AUTHENTICATION_REQUIRED = 0x00;
    private byte SOCKS5_AUTH_METHOD_USERNAME_PASSWORD = 0x02;
    
    private byte SOCKS5_CMD_REPLY_SUCCEEDED = 0x00;
    private byte SOCKS5_CMD_REPLY_GENERAL_SOCKS_SERVER_FAILURE = 0x01;
    private byte SOCKS5_CMD_REPLY_CONNECTION_NOT_ALLOWED_BY_RULESET = 0x02;
    private byte SOCKS5_CMD_REPLY_NETWORK_UNREACHABLE = 0x03;
    private byte SOCKS5_CMD_REPLY_HOST_UNREACHABLE = 0x04;
    private byte SOCKS5_CMD_REPLY_CONNECTION_REFUSED = 0x05;
    private byte SOCKS5_CMD_REPLY_TTL_EXPIRED = 0x06;
    private byte SOCKS5_CMD_REPLY_COMMAND_NOT_SUPPORTED = 0x07;
    private byte SOCKS5_CMD_REPLY_ADDRESS_TYPE_NOT_SUPPORTED = 0x08;
    
    private byte SOCKS5_ADDRTYPE_IPV4 = 0x01;
    private byte SOCKS5_ADDRTYPE_DOMAIN_NAME = 0x03;
    private byte SOCKS5_ADDRTYPE_IPV6 = 0x04;

    private byte SOCKS5_CMD_CONNECT = 0x01;

    private SocksAuthentication _proxyAuthMethod;
    
    private String _proxyUserName;
    private String _proxyPassword;
   
    boolean sentAuth = false;
    boolean goodAuth = false;
    
    boolean proxied = false;
    
    boolean sentConnectCommand = false;
    boolean connectCommandResult = false;

	private FeedClientDDF feedClient;

	private DDF_SocksProxy proxySettings;
    
	private static final Logger log = LoggerFactory
			.getLogger(SocksClientHandler.class);
	
	public SocksClientHandler(FeedClientDDF feed, DDF_SocksProxy proxySettings) {
		super();

		log.debug("SocksClientHandler Init");

		this.feedClient = feed;
		this.proxySettings = proxySettings;
	}
	
	  @Override
	  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		 if(!(e.getMessage() instanceof ChannelBuffer)){
			  return;
		  }

		  ChannelBuffer msg = (ChannelBuffer) e.getMessage();

		  msg = msg.slice(msg.readerIndex(), msg.writerIndex());

		  //  SERVER AUTHENTICATION RESPONSE
		  //  The server selects from one of the methods given in METHODS, and
		  //  sends a METHOD selection message:
		  //
		  //     +----+--------+
		  //     |VER | METHOD |
		  //     +----+--------+
		  //     | 1  |   1    |
		  //     +----+--------+
		  //
		  //  If the selected METHOD is X'FF', none of the methods listed by the
		  //  client are acceptable, and the client MUST close the connection.
		  //
		  //  The values currently defined for METHOD are:
		  //   * X'00' NO AUTHENTICATION REQUIRED
		  //   * X'01' GSSAPI
		  //   * X'02' USERNAME/PASSWORD
		  //   * X'03' to X'7F' IANA ASSIGNED
		  //   * X'80' to X'FE' RESERVED FOR PRIVATE METHODS
		  //   * X'FF' NO ACCEPTABLE METHODS

		  //  receive the server response 

		  if(lastStep==null){

			  byte acceptedAuthMethod = msg.getByte(1);

			  // if the server does not accept any of our supported authentication methods then throw an error
			  if (acceptedAuthMethod == SOCKS5_AUTH_METHOD_REPLY_NO_ACCEPTABLE_METHODS){
				 e.getChannel().close();
				 throw new SocksProxyException("The proxy destination does not accept the supported proxy client authentication methods.");
			  }

			  if(acceptedAuthMethod == SOCKS5_AUTH_METHOD_NO_AUTHENTICATION_REQUIRED){
				 
				  log.debug("Using no authentication for SOCKS5.");

				  //send connect command
				  
				  SendCommand(e.getChannel(), SOCKS5_CMD_CONNECT);
				  
				  lastStep = Step.CONNECT;
				  
				  return;
			  }

			  if (acceptedAuthMethod == SOCKS5_AUTH_METHOD_USERNAME_PASSWORD && _proxyAuthMethod == SocksAuthentication.None){
				  e.getChannel().close();
				  throw new SocksProxyException("The proxy destination requires a username and password for authentication, none are set");
			  }

			  if (acceptedAuthMethod == SOCKS5_AUTH_METHOD_USERNAME_PASSWORD) {

				  log.debug("Using username and password for SOCKS5 authentication");

				  _proxyUserName = proxySettings.getProxyUsername();
				  _proxyPassword = proxySettings.getProxyPassword();
				  
				  // USERNAME / PASSWORD SERVER REQUEST
				  // Once the SOCKS V5 server has started, and the client has selected the
				  // Username/Password Authentication protocol, the Username/Password
				  // subnegotiation begins.  This begins with the client producing a
				  // Username/Password request:
				  //
				  //       +----+------+----------+------+----------+
				  //       |VER | ULEN |  UNAME   | PLEN |  PASSWD  |
				  //       +----+------+----------+------+----------+
				  //       | 1  |  1   | 1 to 255 |  1   | 1 to 255 |
				  //       +----+------+----------+------+----------+

				  // create a data structure (binary array) containing credentials
				  // to send to the proxy server which consists of clear username and password data
				  byte[] credentials = new byte[_proxyUserName.length() + _proxyPassword.length() + 3];

				  credentials[0] = SOCKS5_VERSION_NUMBER;
				  credentials[1] = (byte)_proxyUserName.length(); 

				  System.arraycopy(_proxyUserName.getBytes(), 0, credentials,2, _proxyUserName.length());

				  credentials[_proxyUserName.length() + 2] = (byte)_proxyPassword.length();

				  System.arraycopy(_proxyPassword.getBytes(), 0, credentials, _proxyUserName.length() + 3, _proxyPassword.length()); 

				  // USERNAME / PASSWORD SERVER RESPONSE
				  // The server verifies the supplied UNAME and PASSWD, and sends the
				  // following response:
				  //
				  //   +----+--------+
				  //   |VER | STATUS |
				  //   +----+--------+
				  //   | 1  |   1    |
				  //   +----+--------+
				  //
				  // A STATUS field of X'00' indicates success. If the server returns a
				  // `failure' (STATUS value other than X'00') status, it MUST close the
				  // connection.

				  // transmit credentials to the proxy server
				  
				  e.getChannel().write(ChannelBuffers.wrappedBuffer(credentials));

				  lastStep = Step.AUTH;
				  
				  return;

			  }

			  // read the response from the proxy server

		  }else if(lastStep==Step.AUTH){

			  byte authResponse =  msg.getByte(1);
			  
			  log.debug("Proxy authentication resp {} " , authResponse);

			  // check to see if the proxy server accepted the credentials
			  if (authResponse != 0) {
				  
				  e.getChannel().close();
				  
				  log.debug("Proxy authentication failure: {}", authResponse);
				  
				  throw new SocksProxyException("Proxy authentication failure - The proxy server has " +
				  		"reported that the userid and/or password is not valid.");
				  
			  }else{

				  log.debug("Proxy authentication success: {}", authResponse);
				  
				  // send a connect command to the proxy server for destination host and port
				  
				  SendCommand(e.getChannel(), SOCKS5_CMD_CONNECT);
				  
				  lastStep = Step.CONNECT;
				  
				  return;
			  }
			  
		  }

		  // read connect command response

		  //  PROXY SERVER RESPONSE
		  //  +----+-----+-------+------+----------+----------+
		  //  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
		  //  +----+-----+-------+------+----------+----------+
		  //  | 1  |  1  | X'00' |  1   | Variable |    2     |
		  //  +----+-----+-------+------+----------+----------+
		  //
		  // * VER protocol version: X'05'
		  // * REP Reply field:
		  //   * X'00' succeeded
		  //   * X'01' general SOCKS server failure
		  //   * X'02' connection not allowed by ruleset
		  //   * X'03' Network unreachable
		  //   * X'04' Host unreachable
		  //   * X'05' Connection refused
		  //   * X'06' TTL expired
		  //   * X'07' Command not supported
		  //   * X'08' Address itemType not supported
		  //   * X'09' to X'FF' unassigned
		  //* RSV RESERVED
		  //* ATYP address itemType of following address

		  // return this channel for DDF3

		  if(lastStep==Step.CONNECT){

			  byte replyCode = msg.getByte(1);

			  if (replyCode == SOCKS5_CMD_REPLY_SUCCEEDED){
				  
				  log.debug("Connect Command SUCCESS :  {}" , replyCode);

				  connectCommandResult = true;
				  
			  }else{
				  HandleProxyCommandError(msg.array(), 
						  proxySettings.getFeedServer().getPrimary(), 7500);
				  
				  connectCommandResult = false;
			  }
			  
			  lastStep = Step.BIND;
			  
			  return;
		  }


		  // if our connection has been proxied, allow the DDF handlers
		  // to process messages from JERQ

		  if(lastStep == Step.BIND && proxied == false){
			  feedClient.setProxiedChannel(ctx, e, true);
			  proxied = true;
		  }
		  
		  if(lastStep == Step.BIND && proxied){
			  ctx.sendUpstream(e);
		  }

	  }
	  
	  private void SendCommand(Channel channel, byte command) throws Exception{
   
		  if(proxySettings.getFeedServer()==null){
			  throw new SocksProxyException("No destination server set");
		  }
		  
		  String destinationHost ="174.129.40.242";
		  Integer destinationPort = 7500;
		  
          byte addressType = GetDestAddressType(destinationHost);
          byte[] destAddr = GetDestAddressBytes(addressType, destinationHost);
          byte[] destPort = GetDestPortBytes(destinationPort);

          //  The connection request is made up of 6 bytes plus the
          //  length of the variable address byte array
          //
          //  +----+-----+-------+------+----------+----------+
          //  |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
          //  +----+-----+-------+------+----------+----------+
          //  | 1  |  1  | X'00' |  1   | Variable |    2     |
          //  +----+-----+-------+------+----------+----------+
          //
          // * VER protocol version: X'05'
          // * CMD
          //   * CONNECT X'01'
          //   * BIND X'02'
          //   * UDP ASSOCIATE X'03'
          // * RSV RESERVED
          // * ATYP address itemType of following address
          //   * IP V4 address: X'01'
          //   * DOMAINNAME: X'03'
          //   * IP V6 address: X'04'
          // * DST.ADDR desired destination address
          // * DST.PORT desired destination port in network octet order            

          byte[] request = new byte[4 + destAddr.length + 2];
          request[0] = SOCKS5_VERSION_NUMBER;
          request[1] = command;
          request[2] = SOCKS5_RESERVED;
          request[3] = addressType;
          System.arraycopy(destAddr, 0, request, 4, destAddr.length);
          System.arraycopy(destPort, 0, request, 4 + destAddr.length, destPort.length);
          
          // send connect request.
          channel.write(ChannelBuffers.wrappedBuffer(request));
      
          sentConnectCommand = true;
      
      }
	  
	  private byte GetDestAddressType(String host) {

         boolean isIp = isAnIP(host);

         if (!isIp) 
         {
          	 System.out.println("Address is domain");
        	 return SOCKS5_ADDRTYPE_DOMAIN_NAME;
         }
         
         InetAddress inet = null;
         
         try {
			inet = InetAddress.getByName(host);
         } catch (UnknownHostException e) {
			return -1;
         }
          
        if(inet instanceof Inet4Address){
        	return SOCKS5_ADDRTYPE_IPV4;
        }else if(inet instanceof Inet6Address){
        	return SOCKS5_ADDRTYPE_IPV6;
        }
        
    	log.error("The host addess " + host + " is not a supported address type.  " +
    			"The supported types are domainm, IPV4 and InterNetworkV6(TODO).");
    	
    	return -1;
      }
	  
	  private byte[] GetDestAddressBytes(byte addressType, String host){
		  
          if (addressType == SOCKS5_ADDRTYPE_IPV4){
        	  
        	  InetAddress inet = null;
        	  try {
        			inet = InetAddress.getByName(host);
        		} catch (UnknownHostException e) {
        			e.printStackTrace();
        			return null;
        		}
        		
          	byte [] binaryIP = inet.getAddress();
              
          	return binaryIP;
          	
          }else if(addressType == SOCKS5_ADDRTYPE_IPV6){
        	  
        	  // TODO
        	  
        	  return null;
        	  
          }else if(addressType == SOCKS5_ADDRTYPE_DOMAIN_NAME){
        	  
              byte[] bytes = new byte[host.getBytes().length + 1]; 
              
        	  //  create a byte array to hold the host name bytes plus one byte to store the length
              //  if the address field contains a fully-qualified domain name.  The first
              //  octet of the address field contains the number of octets of name that
              //  follow, there is no terminating NUL octet.
              
              // byte[] host_length_byte = ByteBuffer.allocate(1).putInt(host.length()).array();
              // byte[] host_byte = ByteBuffer.allocate(1).p(host.length()).array();
              
              bytes[0] = (byte) host.getBytes().length;
              System.arraycopy( host.getBytes(), 0, bytes, 1, host.getBytes().length);

              return bytes;
              
          }
        
          return null; 

      }

	  @Override
	  public void channelConnected(final ChannelHandlerContext ctx,
			final ChannelStateEvent e) throws Exception {

		  log.debug("Proxy channel connected");

		  determineClientAuthMethod();
		  
		  NegotiateServerAuthMethod(e.getChannel());

	  }

		@Override
		public void channelDisconnected(final ChannelHandlerContext ctx,
				final ChannelStateEvent e) throws Exception {

			ctx.sendUpstream(e);

		}

		@Override
		 public void exceptionCaught(
		            ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			
			// general socks failure
			feedClient.postEvent(FeedEvent.LINK_CONNECT_PROXY_TIMEOUT);
			feedClient.setProxiedChannel(null, null, false);

		}
	    
		private void determineClientAuthMethod(){
			
			if(proxySettings.getProxyUsername()!=null && 
					proxySettings.getProxyPassword()!=null){
				_proxyAuthMethod = SocksAuthentication.UsernamePassword;
			}else{
				_proxyAuthMethod = SocksAuthentication.None;
			}
			
		}
	  
		private void NegotiateServerAuthMethod(Channel channel){
			

	        // SERVER AUTHENTICATION REQUEST
	        // The client connects to the server, and sends a version
	        // identifier/method selection message:
	        //
	        //      +----+----------+----------+
	        //      |VER | NMETHODS | METHODS  |
	        //      +----+----------+----------+
	        //      | 1  |    1     | 1 to 255 |
	        //      +----+----------+----------+
			
			byte[] authRequest = new byte[4];
			
			authRequest[0] = SOCKS5_VERSION_NUMBER;
	        authRequest[1] = SOCKS5_AUTH_NUMBER_OF_AUTH_METHODS_SUPPORTED;
	        authRequest[2] = SOCKS5_AUTH_METHOD_NO_AUTHENTICATION_REQUIRED; 
	        authRequest[3] = SOCKS5_AUTH_METHOD_USERNAME_PASSWORD; 

	        // send the request to the server specifying authentication 
	        // types supported by the client.
	       
	        channel.write( ChannelBuffers.wrappedBuffer(authRequest));
			   
		}
		
      private byte[] GetDestPortBytes(int value){

          byte[] bytes = new byte[2];

          bytes[0] = (byte) (value / 256);
          bytes[1] = (byte) (value % 256);
    	  
          return bytes;
      }
      
	  public static boolean isAnIP (String sip){
		  
	        String [] parts = sip.split ("\\.");
	        
	        for (String s : parts) {
	            int i =0;
	           
	            try {
					i= Integer.parseInt (s);
				} catch (NumberFormatException e) {
					return false;
				}
	            
	            if (i < 0 || i > 255){
	                return false;
	            }
	        }
	        
	        return true;
	  } 
	  
	  private void HandleProxyCommandError(byte[] response, String destinationHost, int destinationPort) throws SocksProxyException{
		  String proxyErrorText;
		  
		  byte replyCode = response[1];
		  byte addrType = response[3];
		  
		  String addr = "";
		  Integer port = 0;

		  if(addrType == SOCKS5_ADDRTYPE_DOMAIN_NAME){
			  int addrLen = response[4];
			  byte[] addrBytes = new byte[addrLen];
			  for (int i = 0; i < addrLen; i++)
				  addrBytes[i] = response[i + 5];
			  addr = new String(addrBytes);
			  byte[] portBytesDomain = new byte[2];
			  portBytesDomain[0] = response[6 + addrLen];
			  portBytesDomain[1] = response[5 + addrLen];
			  ByteBuffer bb = ByteBuffer.wrap(portBytesDomain);
			  port = bb.getInt(); 

		  }else if (addrType == SOCKS5_ADDRTYPE_IPV4){

			  byte[] ipv4Bytes = new byte[4];
			  for (int i = 0; i < 4; i++)
				  ipv4Bytes[i] = response[i + 4];

			  byte[] portBytesIpv4 = new byte[2];
			  portBytesIpv4[0] = response[9];
			  portBytesIpv4[1] = response[8];

			  InetAddress ipv4 = null;
			  try {
				  ipv4 = InetAddress.getByAddress(ipv4Bytes);
			  } catch (UnknownHostException e1) {
				  e1.printStackTrace();
			  }
			  
			  port = (((0xFF & portBytesIpv4[1]) << 8) + (0xFF & portBytesIpv4[0]));
			  addr = ipv4.toString();

		  }else if (addrType== SOCKS5_ADDRTYPE_IPV6){
			  byte[] ipv6Bytes = new byte[16];
			  for (int i = 0; i < 16; i++)
				  ipv6Bytes[i] = response[i + 4];

			  //IPAddress ipv6 = new IPAddress(ipv6Bytes);
			  InetAddress ipv6 = null;

			  try {
				  ipv6 = InetAddress.getByAddress(ipv6Bytes);
			  } catch (UnknownHostException e) {
				  e.printStackTrace();
			  }

			  byte[] portBytesIpv6 = new byte[2];
			  portBytesIpv6[0] = response[21];
			  portBytesIpv6[1] = response[20];

			  port = (((0xFF & portBytesIpv6[1]) << 8) + (0xFF & portBytesIpv6[0]));
			  addr = ipv6.toString();

		  }


		  if(replyCode == SOCKS5_CMD_REPLY_GENERAL_SOCKS_SERVER_FAILURE){
			  proxyErrorText = "a general socks destination failure occurred";

		  }else if(replyCode == SOCKS5_CMD_REPLY_CONNECTION_NOT_ALLOWED_BY_RULESET){

			  proxyErrorText = "the connection is not allowed by proxy destination rule set";

		  }else if(replyCode == SOCKS5_CMD_REPLY_NETWORK_UNREACHABLE){
			  proxyErrorText = "the network was unreachable";

		  }else if(replyCode == SOCKS5_CMD_REPLY_HOST_UNREACHABLE){
			  proxyErrorText = "the host was unreachable";

		  }else if(replyCode == SOCKS5_CMD_REPLY_CONNECTION_REFUSED){
			  proxyErrorText = "the connection was refused by the remote network";

		  }else if(replyCode ==SOCKS5_CMD_REPLY_TTL_EXPIRED){

			  proxyErrorText = "the time to live (TTL) has expired";

		  }else if(replyCode == SOCKS5_CMD_REPLY_COMMAND_NOT_SUPPORTED){

			  proxyErrorText = "the command issued by the proxy client is not supported by the proxy destination";

		  }else if(replyCode == SOCKS5_CMD_REPLY_ADDRESS_TYPE_NOT_SUPPORTED){

			  proxyErrorText = "the address type specified is not supported";

		  }else{
			  proxyErrorText = String.format("that an unknown reply with the code value '%s' was received " +
			  		"by the destination", Byte.toString(replyCode));

		  }

		  @SuppressWarnings("unused")
		  String exceptionMsg = String.format("%s destination host %s port number %s.  The destination reported " +
		  		"the host as %s port %s type of %s.", proxyErrorText, destinationHost, destinationPort, addr, Integer.toString(port), addrType);

		  feedClient.postEvent(FeedEvent.LINK_CONNECT_PROXY_TIMEOUT);
		  feedClient.setProxiedChannel(null, null, false);
		  
		  log.error("Socks5 Error : {}", proxyErrorText);
		  
		  // don't blow up, allow user forced reconnect
		  
		  //throw new SocksProxyException(exceptionMsg);

	  }

}
