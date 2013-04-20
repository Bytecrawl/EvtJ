package bytecrawl.evtj.server.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import bytecrawl.evtj.server.EvtJServer;


public class DispatcherHandler implements Handler {
	
	private EvtJServer server;
	private Selector selector;
	private Iterator<SelectionKey> selector_iterator;
	private SocketChannel client_channel;
	private SelectionKey selected_key;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	private String request;
			
	public DispatcherHandler(EvtJServer server) {
		this.server = server;
		selector = server.getSelector();
	}
	
	private void accept(SelectionKey key)
	{
		try
		{
			client_channel = ((ServerSocketChannel) key.channel()).accept();
			client_channel.configureBlocking(false);
			client_channel.register(selector, SelectionKey.OP_READ);
			server.newAcceptedClient(client_channel);
		}catch(IOException e) {
			System.out.println("Error accepting connection");
			e.printStackTrace();
		}
	}
	
	private void read(SelectionKey key) throws IOException
	{
		client_channel = (SocketChannel) key.channel();
		buffer.clear();

		try {
			client_channel = (SocketChannel) key.channel();
			buffer.clear();
			if (client_channel.read(buffer) != -1) {
				buffer.flip();
				request = new String(buffer.array(), buffer.position(), buffer.remaining());
    			server.queueRequest(client_channel, request);
    			System.out.println("Accepted request from "+client_channel.getLocalAddress().toString());
    		}else{
    			throw new IOException("Disconnected");
    		}
	    } catch (IOException e) {
	    	server.newDisconnectedClient(client_channel);
	        key.cancel();
	        client_channel.close();
	        return;
	    }
	}
	
	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public void onRun() {
		try {
			selector.select();
			selector_iterator = selector.selectedKeys().iterator();
			while (selector_iterator.hasNext()) {
				selected_key = selector_iterator.next();
				selector_iterator.remove();
				if(selected_key.isAcceptable()) {
					accept(selected_key);
				}else if(selected_key.isReadable()) {
					read(selected_key);
				}
			}
		} catch (IOException e) {
			System.out.println("Unexpected error on DispatchHandler onRun");
			e.printStackTrace();
		}
	}

}
