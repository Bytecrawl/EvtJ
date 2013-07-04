package bytecrawl.evtj.server;

import bytecrawl.evtj.server.handlers.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvtJExecutor extends Thread implements Runnable {

    private Logger logger = LoggerFactory.getLogger("EvtJServer");
	private EvtJServer server;
	private Handler handler;

	/**
	 * @param server
	 *            reference to the server that spawns the executor
	 * @param handler
	 *            reference to the handler that is going to be run by this
	 *            executor.
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
	private void sleep() {
		try {
			Thread.sleep(0, 100);
		} catch (InterruptedException e) {
			this.interrupt();
		}
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
                if(pause) {
                    handler.onPause();
                    while(pause) {
                        wait();
                    }
                    handler.onResume();
                }
                handler.onRun();
            }
            handler.onStop();
        }catch(Exception e) {

        }
	}

    private boolean pause = false;

    public synchronized void pause() {
        pause = true;
    }

    public synchronized void unpause() {
        pause = false;
        this.notify();
    }

}
