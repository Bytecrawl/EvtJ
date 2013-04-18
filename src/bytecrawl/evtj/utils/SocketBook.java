package bytecrawl.evtj.utils;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class SocketBook extends LinkedList<SocketChannel> {

	private static final long serialVersionUID = 1L;
	
	public SocketBook()
	{
		super();
	}
	
	public synchronized SocketChannel get(int i)
	{
		return super.get(i);
	}
	
	public synchronized boolean add(SocketChannel s)
	{
		return super.add(s);
	}

}
