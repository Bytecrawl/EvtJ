package bytecrawl.evtj.server;

public class State {

    private boolean active;
    private boolean paused;
    private boolean initialising;
    private int starts;
    private int stops;
    private int pauses;
    private int resumes;
    private int initialises;

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

    public int getInitialises() {
        return initialises;
    }

}
