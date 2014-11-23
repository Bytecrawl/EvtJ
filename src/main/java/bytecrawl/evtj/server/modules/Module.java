package bytecrawl.evtj.server.modules;

import bytecrawl.evtj.server.requests.Request;

public interface Module {

    public void onPause();

    public void onResume();

    public void onStart();

    public void onStop();

    public void serveRequest(Request request);

}