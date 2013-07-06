package bytecrawl.evtj.server.executors;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.handlers.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ClosedSelectorException;

public class EvtJExecutor extends Thread implements Runnable {

    private Logger logger = LoggerFactory.getLogger("EvtJServer");
    private EvtJServer server;
    private Handler handler;
    private boolean pause = false;

    /**
     * @param server  reference to the server that spawns the executor
     * @param handler reference to the handler that is going to be run by this
     *                executor.
     */
    public EvtJExecutor(EvtJServer server, Handler handler) {
        this.server = server;
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * When sleep is interrupted, interrupt the Thread execution.
     */
    private void sleep() throws InterruptedException {
        Thread.sleep(0, 100);
    }

    /**
     * Loop structure to model the states of an executable task/handler.
     */
    public void run() {
        try {
            while (server.isInitialising()) {
                sleep();
            }
            while (server.isActive()) {
                synchronized (this) {
                    if (pause) {
                        handler.onPause();
                        while (pause) {
                            wait();
                        }
                        handler.onResume();
                    }
                }
                handler.onRun();
            }
            handler.onStop();
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
