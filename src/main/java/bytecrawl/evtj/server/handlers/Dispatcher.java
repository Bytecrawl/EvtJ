package bytecrawl.evtj.server.handlers;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJClient;
import bytecrawl.evtj.utils.EvtJConfiguration;
import bytecrawl.evtj.utils.EvtJRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Dispatcher implements Handler {

    private int BUFFER_SIZE = EvtJConfiguration.getInt(EvtJConfiguration.CONFIG_WORKER_POOL);
    private String SPLIT_SEQUENCE = EvtJConfiguration.get(EvtJConfiguration.CONFIG_SPLIT_SEQUENCE);
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Logger logger = LoggerFactory.getLogger("EvtJServer");
    private int readedBytes;
    private String request;
    private String[] requestArray;
    private SelectionKey selectedKey;
    private Selector selector;
    private Iterator<SelectionKey> selectorIterator;
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
            selectorIterator = selector.selectedKeys().iterator();
            while (selectorIterator.hasNext()) {
                selectedKey = selectorIterator.next();
                selectorIterator.remove();

                /** Only handle Acceptable and Readable */
                if (selectedKey.isAcceptable()) {
                    accept(selectedKey);
                } else if (selectedKey.isReadable()) {
                    read(selectedKey);
                }
            }
        } catch (ClosedChannelException closed_e) {
            server.newDisconnection();
            selectedKey.cancel();
        } catch (IOException e) {
            server.newDisconnection();
            selectedKey.cancel();
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
        readedBytes = client.getChannel().read(buffer);
        buffer.flip();

        if (readedBytes != -1) {
            request = new String(buffer.array(), buffer.position(),
                    buffer.remaining());

            /** If more to read, keep building the request */
            while (readedBytes > 0) {
                buffer.clear();
                readedBytes = client.getChannel().read(buffer);
                if (readedBytes == -1)
                    throw new IOException();
                buffer.flip();
                request += new String(buffer.array(), buffer.position(),
                        buffer.remaining());
                ;
            }

            /** Split in case of multiple requests */
            requestArray = request.split(SPLIT_SEQUENCE);

            for (String requestText : requestArray) {
                EvtJRequest request = new EvtJRequest(client, requestText);
                server.newServedRequest();
                server.queue(request);
                logger.debug("Accepted request from " + client.getIP() + ": "
                        + requestText);
            }
        } else {
            throw new ClosedChannelException();
        }
    }

}
