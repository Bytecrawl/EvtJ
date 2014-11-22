package bytecrawl.evtj.tests.mocks;

import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Client;

import java.io.IOException;
import java.nio.ByteBuffer;

public class EchoModule extends Module {

    @Override
    public void serveRequest(Client client, String request) {
        request = request + "\n";
        try {
            client.write(ByteBuffer.wrap(request.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
