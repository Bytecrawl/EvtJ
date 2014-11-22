package bytecrawl.evtj.server.requests;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.executors.Executable;
import bytecrawl.evtj.server.EvtJServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestDispatcher implements Executable {

    private int BUFFER_SIZE = Configuration.getInt(Configuration.CFG_WORKER_POOL);
    private String SPLIT_SEQUENCE = Configuration.get(Configuration.CFG_SPLIT_SEQUENCE);
    private int SPLIT_SEQUENCE_LEN = SPLIT_SEQUENCE.length();
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Logger logger = LoggerFactory.getLogger("EvtJServer");
    private SelectionKey selectedKey;
    private Selector selector;
    private EvtJServer server;

    public RequestDispatcher(EvtJServer server) {
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
        Client client = new Client(((ServerSocketChannel) key.channel()).accept());
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
            Iterator<SelectionKey> selectorIterator = selector.selectedKeys().iterator();
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
            onStop();
        } catch (IOException e) {
            onStop();
        }
    }

    public void onStop() {
        try {
            selector.close();
        } catch (IOException e) {
            logger.error("Error closing the selector", e);
        }
    }

    private void read(SelectionKey key) throws IOException {
        Client client = new Client((SocketChannel) key.channel());
        String request = "";
        int readedBytes = 1;

        while (readedBytes > 0) {
            buffer.clear();
            readedBytes = client.getChannel().read(buffer);
            buffer.flip();
            if (readedBytes == -1) {
                key.cancel();
                server.newDisconnection(client);
                return;
            }
            request += new String(buffer.array(), buffer.position(), buffer.remaining());
        }

        /** Fetch attachment and append the readed content */
        String attachment;
        if (key.attachment() != null) attachment = (String) key.attachment();
        else attachment = "";
        attachment += request;

        /** If there is no split sequence attach the request and return */
        if (!attachment.contains(SPLIT_SEQUENCE)) {
            key.attach(attachment);
            return;
        }

        /** Split in case of multiple requests */
        List<String> requestList = new ArrayList<String>();
        while (attachment.contains(SPLIT_SEQUENCE)) {
            int match = attachment.indexOf(SPLIT_SEQUENCE);
            int length = attachment.length();
            requestList.add(attachment.substring(0, match));
            attachment = attachment.substring(match + SPLIT_SEQUENCE_LEN, length);
        }

        if (attachment.length() > 0) key.attach(attachment);
        else key.attach(null);

        for (String requestText : requestList) {
            Request req = new Request(client, requestText);
            server.newServedRequest();
            server.queue(req);
            logger.info("Request from " + client.getAddress() + ": " + requestText);
        }
    }

}
