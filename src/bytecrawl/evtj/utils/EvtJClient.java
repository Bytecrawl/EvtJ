package bytecrawl.evtj.utils;

import java.nio.channels.SocketChannel;

public class EvtJClient {

	SocketChannel channel;
	
	public EvtJClient(SocketChannel channel)
	{
		this.channel = channel;
	}
	
	public SocketChannel getChannel() { return channel; }
	
}
