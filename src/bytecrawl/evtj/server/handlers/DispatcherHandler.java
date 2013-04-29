package bytecrawl.evtj.server.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJClient;


public class DispatcherHandler implements HandlerI {
	
	private EvtJServer server;
	private Selector selector;
	private Iterator<SelectionKey> selector_iterator;
	private EvtJClient client;
	private SelectionKey selected_key;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	private String request;
	private Logger logger = Logger.getLogger("app");
			
	public DispatcherHandler(EvtJServer server) {
		this.server = server;
		
		selector = server.getSelector();
	}
	
	private void accept(SelectionKey key) throws IOException
	{
		client = new EvtJClient(((ServerSocketChannel) key.channel()).accept());
		client.getChannel().configureBlocking(false);
		client.getChannel().register(selector, SelectionKey.OP_READ);
		server.newAcceptedClient(client);
	}
	
	private void read(SelectionKey key) throws IOException
	{
		try {
			client = new EvtJClient((SocketChannel) key.channel());
			buffer.clear();
			
			int read_bytes = client.getChannel().read(buffer);
			buffer.flip();
			if(read_bytes != -1)
			{
				request = new String(buffer.array(), buffer.position(), buffer.remaining());
				while(read_bytes > 0)
				{
					buffer.clear();
					read_bytes = client.getChannel().read(buffer);
					if(read_bytes == -1) throw new IOException();
					buffer.flip();
					String partial = new String(buffer.array(), buffer.position(), buffer.remaining());
					request +=partial;
				}
				String[] request_array = request.split("\n");
				for(String req : request_array)
				{
					server.newServedRequest();
					server.queue(client, req);
					logger.debug("Accepted request from "+client.getIP()+": "+req);
				}	
			}else{
				throw new IOException();
			}
	    } catch (IOException e) {
	    	server.newDisconnectedClient(client);
	        key.cancel();
	        client.getChannel().close();
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
			logger.error("Unexpected error on DispatchHandler onRun");
			e.printStackTrace();
		}
	}

}
