package bytecrawl.evtj.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ClosedSelectorException;

public class ExecutionThread extends Thread implements Runnable {

    private Logger logger = LoggerFactory.getLogger("EvtJServer");
    private bytecrawl.evtj.server.State state;
    private Executable executable;
    private boolean pause = false;

    /**
     * @param state reference to the state that spawns the executor
     */
    public ExecutionThread(bytecrawl.evtj.server.State state, Executable executable) {
        this.state = state;
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
            while (state.isInitialising()) {
                sleep();
            }
            while (state.isActive()) {
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
            System.out.println("Interrupted");
        } catch (ClosedSelectorException s) {
            System.out.println("Closed");
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
