package bytecrawl.evtj.server.modules;

import bytecrawl.evtj.utils.EvtJRequest;

public abstract class EvtJModuleWorker implements Runnable {

    private EvtJRequest request;

    public EvtJRequest getEvtJRequest() {
        return request;
    }

    public void setEvtJRequest(EvtJRequest request) {
        this.request = request;
    }

}
