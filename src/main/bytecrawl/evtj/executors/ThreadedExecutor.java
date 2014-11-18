package bytecrawl.evtj.executors;

import bytecrawl.evtj.server.EvtJServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ClosedSelectorException;

public class ThreadedExecutor extends Thread implements Runnable {

    private Logger logger = LoggerFactory.getLogger("EvtJServer");
    private EvtJServer server;
    private Executable executable;
    private boolean pause = false;

    /**
     * @param server reference to the server that spawns the executor
     */
    public ThreadedExecutor(EvtJServer server) {
        this.server = server;
    }

    /**
     * @param executable executable reference to the executable that is going to be run by this
     *                   executor.
     */
    public void setExecutable(Executable executable) {
        this.executable = executable;
    }

    public Executable getExecutable() {
        return executable;
    }

    /**
     * When sleep is interrupted, interrupt the Thread execution.
     */
    private void sleep() throws InterruptedException {
        Thread.sleep(0, 100);
    }

    /**
     * Loop structure to model the states of an executable task/executable.
     */
    public void run() {
        try {
            while (server.isInitialising()) {
                sleep();
            }
            while (server.isActive()) {
                synchronized (this) {
                    if (pause) {
                        executable.onPause();
                        while (pause) {
                            wait();
                        }
                        executable.onResume();
                    }
                }
                executable.onRun();
            }
            executable.onStop();
        } catch (InterruptedException e) {

        } catch (ClosedSelectorException s) {

        }
    }

    public synchronized void pause() {
        pause = true;
    }

    public synchronized void unpause() {
        pause = false;
        this.notify();
    }

}
