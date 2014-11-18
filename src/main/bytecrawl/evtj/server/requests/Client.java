package bytecrawl.evtj.server.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    private SocketChannel channel;
    private String address;
    private Logger logger = LoggerFactory.getLogger("EvtJServer");

    public Client() {
    }

    ;

    public Client(SocketChannel channel) throws IOException {
        this.channel = channel;
        this.address = channel.socket().getRemoteSocketAddress().toString();
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public String getAddress() {
        return address;
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Error closing channel", e);
        }
    }

    public void write(ByteBuffer buffer) throws IOException {
        channel.write(buffer);
    }

    public void read(ByteBuffer buffer) throws IOException {
        channel.read(buffer);
    }
}
