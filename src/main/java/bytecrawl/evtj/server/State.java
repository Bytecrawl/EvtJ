package bytecrawl.evtj.server;

import bytecrawl.evtj.server.requests.Client;
import bytecrawl.evtj.server.requests.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class State {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private boolean active;
    private boolean paused;
    private boolean initialising;

    private int starts;
    private int stops;
    private int pauses;
    private int resumes;
    private int initialises;

    private int connections;
    private int servedRequests;

    public State() {
        loadInitialState();
    }

    private void loadInitialState() {
        active = false;
        paused = false;
        initialising = false;

        starts = 0;
        pauses = 0;
        stops = 0;
        resumes = 0;
        initialises = 0;

        connections = 0;
        servedRequests = 0;
    }

    public void initialised() {
        initialising = true;
        paused = false;
        active = false;

        initialises++;
    }

    public void resumed() {
        initialising = false;
        active = true;
        paused = false;

        resumes++;
    }

    public void started() {
        active = true;
        initialising = false;
        paused = false;

        starts++;
    }

    public void stopped() {
        initialising = false;
        paused = false;
        active = false;

        stops++;
    }

    public void paused() {
        active = true;
        paused = true;
        initialising = false;

        pauses++;
    }

    public synchronized void newConnection(Client client) {
        connections++;
        logger.info("Connection accepted from " + client.getAddress() +
                " [ " + connections + " online clients ]");
    }

    public synchronized void newServedRequest(Request request) {
        servedRequests++;
        logger.info("Request from " + request.getClient().getAddress() + ": " + request.getRequest());
    }

    public synchronized void newDisconnection(Client client) {
        connections--;
        logger.info("Disconnection from " + client.getAddress());
    }

    public boolean isActive() {
        return active;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStopped() {
        return !isActive();
    }

    public boolean isInitialising() {
        return initialising;
    }

    public int getStarts() {
        return starts;
    }

    public int getStops() {
        return stops;
    }

    public int getPauses() {
        return pauses;
    }

    public int getResumes() {
        return resumes;
    }

    public int getInitializations() {
        return initialises;
    }

    public int getConnections() {
        return connections;
    }

    public int getServedRequests() {
        return servedRequests;
    }

}
