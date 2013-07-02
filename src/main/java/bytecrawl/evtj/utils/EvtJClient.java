package bytecrawl.evtj.utils;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvtJClient {

	private SocketChannel channel;
	private String IP;
	private Logger logger = LoggerFactory.getLogger("EvtJServer");

	public EvtJClient() {};
	public EvtJClient(SocketChannel channel) throws IOException {
		this.channel = channel;
		this.IP = channel.socket().getRemoteSocketAddress().toString();
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public String getIP() {
		return IP;
	}

	public void close() {
		try {
			channel.close();
		} catch (IOException e) {
			logger.error("Error closing channel", e);
		}
	}
}
