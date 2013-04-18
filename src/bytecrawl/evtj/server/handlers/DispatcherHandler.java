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
	private Iterator<SelectionKey> iter;
	private SocketChannel client_channel;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
			
	public DispatcherHandler(EvtJServer server) {
		this.server = server;
		selector = server.getSelector();
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
			selector.select();
			iter = selector.selectedKeys().iterator();
			while (iter.hasNext()) {
				SelectionKey key = iter.next();
				iter.remove();
				switch (key.readyOps()) {
			    	case SelectionKey.OP_ACCEPT:
			    		client_channel = ((ServerSocketChannel) key.channel()).accept();
			    		client_channel.configureBlocking(false);
			    		client_channel.register(selector, SelectionKey.OP_READ);
			    		break;
			    	case SelectionKey.OP_READ:
			    		client_channel = (SocketChannel) key.channel();
			    		buffer.clear();
			    		if (client_channel.read(buffer) != -1) {
							buffer.flip();
							String cmd = new String(buffer.array(), buffer.position(), buffer.remaining());
			    			server.queueRequest(client_channel, cmd);
			    			client_channel.register(selector,  SelectionKey.OP_READ);
			    		} else {
			    			key.cancel();
			    		}
			    		
			    		break;
			    	default:
			    		System.out.println("unhandled " + key.readyOps());
			    		break;
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
