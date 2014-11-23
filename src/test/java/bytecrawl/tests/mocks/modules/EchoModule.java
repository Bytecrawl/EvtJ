package bytecrawl.tests.mocks.modules;

import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Client;
import bytecrawl.evtj.server.requests.Request;

import java.io.IOException;
import java.nio.ByteBuffer;

public class EchoModule implements Module {

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
        try {
            client.write(ByteBuffer.wrap(request.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccept(Client client) {

    }
}
