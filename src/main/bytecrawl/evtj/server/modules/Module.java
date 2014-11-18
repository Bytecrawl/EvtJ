package bytecrawl.evtj.server.modules;

import bytecrawl.evtj.server.requests.Client;
import bytecrawl.evtj.server.requests.Request;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public abstract class Module implements Runnable, ModuleInterface {

    private Request request;
    private CharsetEncoder encoder;

    public Module() {
        this.encoder = Charset.forName("UTF-8").newEncoder();
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void respondTo(Client recipient, String msg) {
        try {
            recipient.getChannel().write(
                    encoder.encode(CharBuffer.wrap(msg)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void serveRequest(Client client, String request);

    public void run() {
        serveRequest(request.getClient(), request.getRequest());
    }

    public Module getWorker() throws IllegalAccessException, InstantiationException {
        return getClass().newInstance();
    }

}