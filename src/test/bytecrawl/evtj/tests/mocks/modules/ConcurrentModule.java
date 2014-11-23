package bytecrawl.evtj.tests.mocks.modules;

import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Client;
import bytecrawl.evtj.server.requests.Request;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ConcurrentModule implements Module {

    private int counter;

    public ConcurrentModule() {
        counter = 0;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void serveRequest(Request req) {
        Client client = req.getClient();
        String request = req.getRequest() + "\n";
        increase();
        try {
            client.write(ByteBuffer.wrap(request.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void increase() {
        counter += 1;
    }

    public int getCounter() {
        return counter;
    }

}