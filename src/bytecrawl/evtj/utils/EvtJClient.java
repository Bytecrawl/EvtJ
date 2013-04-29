package bytecrawl.evtj.utils;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class EvtJClient {

	private SocketChannel channel;
	private String IP;
	
	public EvtJClient(SocketChannel channel) throws IOException
	{
		this.channel = channel;
		this.IP = channel.getLocalAddress().toString();
	}
	
	public SocketChannel getChannel() { return channel; }
	
	public String getIP() { return IP; }
}
