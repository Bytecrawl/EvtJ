package bytecrawl.tests.mocks;

import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Request;


public class MockModule implements Module {

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
    public void serveRequest(Request request) {

    }
}