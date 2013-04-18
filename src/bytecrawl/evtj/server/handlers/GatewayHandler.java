package bytecrawl.evtj.server.handlers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import bytecrawl.evtj.server.EvtJServer;

public class GatewayHandler implements Handler {

	private EvtJServer server;
	private Selector selector;
	private SocketChannel client_channel;
	
	public GatewayHandler(EvtJServer server) {
		this.server = server;
		selector = server.getSelector();
	}
	
	@Override
	public void onPause() {
		System.out.println("Server paused");
	}

	@Override
	public void onResume() {
		System.out.println("Server resumed");
	}

	@Override
	public void onStop() {

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
					if(key.readyOps() == SelectionKey.OP_ACCEPT)
					{
						iter.remove();
		            	client_channel= ((ServerSocketChannel) key.channel()).accept();
		            	if(server.addClientToBook(client_channel))
		            		System.out.println("New client accepted		[connected clients] = "+server.getConnectedClients());
		            	else
		            		System.out.println("Error accepting connection");
					}
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}