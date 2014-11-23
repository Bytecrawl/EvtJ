package bytecrawl.tests.mocks.modules;

import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Client;
import bytecrawl.evtj.server.requests.Request;

public class CountModule implements Module {

    private int pauses, resumes, starts, stops;

    public CountModule() {
        reset();
    }

    public void reset() {
        pauses = 0;
        resumes = 0;
        starts = 0;
        stops = 0;
    }

    public int getPauses() {
        return pauses;
    }

    public int getResumes() {
        return resumes;
    }

    public int getStarts() {
        return starts;
    }

    public int getStops() {
        return stops;
    }

    @Override
    public void onPause() {
        pauses++;
    }

    @Override
    public void onResume() {
        resumes++;
    }

    @Override
    public void onStart() {
        starts++;
    }

    @Override
    public void onStop() {
        stops++;
    }

    @Override
    public void serveRequest(Request req) {

    }

    @Override
    public void onAccept(Client client) {

    }

}