package bytecrawl.evtj.server.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJClient;
import bytecrawl.evtj.utils.EvtJRequest;

public class Dispatcher implements Handler {

	private final int BUFFER_SIZE = 1024;
	private final String SPLIT_SEQUENCE = "\n";

	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	private Logger logger = LoggerFactory.getLogger("EvtJServer");
	private int read_bytes;
	private String request;
	private String[] request_array;
	private SelectionKey selected_key;
	private Selector selector;
	private Iterator<SelectionKey> selector_iterator;
	private EvtJServer server;

	public Dispatcher(EvtJServer server) {
		this.server = server;
		this.selector = server.getSelector();
	}

	/**
	 * Accept Socket connection from a Selector Key. Notify server about that.
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException {
		EvtJClient client = new EvtJClient(((ServerSocketChannel) key.channel()).accept());
		client.getChannel().configureBlocking(false);
		client.getChannel().register(selector, SelectionKey.OP_READ);
		server.newAcceptedConnection(client);
	}

	public void onPause() {

	}

	public void onResume() {

	}

	public void onRun() {
		try {
			selector.select();
			selector_iterator = selector.selectedKeys().iterator();
			while (selector_iterator.hasNext()) {
				selected_key = selector_iterator.next();
				selector_iterator.remove();
				
				/** Only handle Acceptable and Readable */
				if (selected_key.isAcceptable()) {
					accept(selected_key);
				} else if (selected_key.isReadable()) {
					read(selected_key);
				}
			}
		} catch (ClosedChannelException closed_e) {
			server.newDisconnection();
			selected_key.cancel();
		} catch (IOException e) {
			server.newDisconnection();
			selected_key.cancel();
		}
	}

	public void onStop() {
		try {
			selector.close();
		} catch (IOException e) {
			logger.error("Error closing the selector", e);
		}
	}

	private void read(SelectionKey key) throws ClosedChannelException,
			IOException {
		EvtJClient client = new EvtJClient((SocketChannel) key.channel());
		buffer.clear();
		read_bytes = client.getChannel().read(buffer);
		buffer.flip();
		
		if (read_bytes != -1) {
			request = new String(buffer.array(), buffer.position(),
					buffer.remaining());

			/** If more to read, keep building the request */
			while (read_bytes > 0) {
				buffer.clear();
				read_bytes = client.getChannel().read(buffer);
				if (read_bytes == -1)
					throw new IOException();
				buffer.flip();
				request += new String(buffer.array(), buffer.position(),
						buffer.remaining());
				;
			}

			/** Split in case of multiple requests */
			request_array = request.split(SPLIT_SEQUENCE);
			
			for (String req : request_array) {
				EvtJRequest request = new EvtJRequest(client, req);
				server.newServedRequest();
				server.queue(request);
				logger.debug("Accepted request from " + client.getIP() + ": "
						+ req);
			}
		} else {
			throw new ClosedChannelException();
		}
	}
	
}
