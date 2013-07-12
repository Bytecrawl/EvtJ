package bytecrawl.evtj.server.modules;

import bytecrawl.evtj.utils.EvtJClient;
import bytecrawl.evtj.utils.EvtJRequest;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public abstract class EvtJModuleWorker implements Runnable {

    private EvtJRequest request;
    private CharsetEncoder encoder;

    public EvtJModuleWorker() {
        this.encoder = Charset.forName("UTF-8").newEncoder();
    }

    public EvtJRequest getEvtJRequest() {
        return request;
    }

    public void setEvtJRequest(EvtJRequest request) {
        this.request = request;
    }

    public void respondTo(EvtJClient recipient, String msg) {
        try {
            recipient.getChannel().write(
                    encoder.encode(CharBuffer.wrap(msg)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void serveRequest(EvtJClient client, String request);

    public void run() {
        serveRequest(request.getClient(), request.getRequest());
    }

}
