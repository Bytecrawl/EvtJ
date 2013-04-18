package bytecrawl.evtj.server.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Stack;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.SocketBook;

public class ExpeditorHandler implements Handler {

	private EvtJServer server;
	private SocketBook book;
	private Stack<Integer> disconnections = new Stack<Integer>();
	private SocketChannel client_channel;
	
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	
	public ExpeditorHandler(EvtJServer server)
	{
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
			Thread.sleep(100);
		}catch(InterruptedException e) {
			
		}
		
		book = server.getSocketBook();
		for(int i=0; i<book.size(); i++) {
			try {
				client_channel = book.get(i);
				String test = "HEARTBEAT";
				buffer.put(test.getBytes());
				client_channel.write(buffer);
				buffer.clear();
			}catch(IOException e) {
				disconnections.push(i);
			}
		}
		int i;
		while(disconnections.size() > 0) {
			i = disconnections.pop();
			server.removeSocketFromBook(i);
			System.out.println("Client disconnected in index "+i);
		}
	}

}
