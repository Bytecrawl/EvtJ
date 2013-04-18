package bytecrawl.evtj.server.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.SocketBook;

public class EventHandler implements Handler {
	
	private EvtJServer server;
	private SocketChannel s;
	private SocketBook book;
	private BufferedReader br;
	private BufferedWriter bw;
	private ByteBuffer buffer;
	private Selector selector;
	private SocketChannel client_channel;
	
	public EventHandler(EvtJServer server) {
		this.server = server;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRun() {
		
		try {
			while (true) {
				selector = server.getSelector();
				selector.select();
				Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					if(key.readyOps() == SelectionKey.OP_READ)
					{
						client_channel = (SocketChannel) key.channel();
						iter.remove();
		            	server.registerRead(client_channel);
		            	ProtocolHandler.run(server, client_channel);
					}
				}
			}
		}catch(IOException e) {
			//e.printStackTrace();
		}

		
		book = server.getSocketBook();
		for(int i=0; i<book.size(); i++)
		{
			try {
				s = book.get(i);
				ProtocolHandler.run(server, s);
			}catch(IOException e){
				System.out.println("IOException on EventHandler");
			}
		}
	}
}