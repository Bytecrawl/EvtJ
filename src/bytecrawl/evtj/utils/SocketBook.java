package bytecrawl.evtj.utils;

import java.net.Socket;
import java.util.LinkedList;

public class SocketBook extends LinkedList<Socket> {

	private static final long serialVersionUID = 1L;
	
	public SocketBook()
	{
		super();
	}
	
	public synchronized Socket get(int i)
	{
		return super.get(i);
	}
	
	public synchronized boolean add(Socket s)
	{
		return super.add(s);
	}

}
