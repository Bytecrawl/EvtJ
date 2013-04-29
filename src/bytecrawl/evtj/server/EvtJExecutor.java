package bytecrawl.evtj.server;

import bytecrawl.evtj.server.handlers.HandlerI;

public class EvtJExecutor extends Thread implements Runnable {

	private EvtJServer server;
	private HandlerI handler;

	/**
	 * @param server
	 *            reference to the server that spawns the executor
	 * @param handler
	 *            reference to the handler that is going to be run by this
	 *            executor.
	 */
	public EvtJExecutor(EvtJServer server, HandlerI handler) {
		this.server = server;
		this.handler = handler;
	}

	public HandlerI getHandler() {
		return handler;
	}

	/**
	 * When sleep is interrupted, interrupt the Thread execution.
	 */
	private void sleep() {
		try {
			Thread.sleep(0, 500);
		} catch (InterruptedException e) {
			this.interrupt();
		}
	}

	/**
	 * Loop structure to model the states of an executable task/handler.
	 */
	public void run() {
		while (server.isInitialising()) {
			sleep();
		}
		while (server.isActive()) {
			if (server.isPaused()) {
				handler.onPause();
				while (server.isPaused()) {
					sleep();
				}
				handler.onResume();
			}
			handler.onRun();
			sleep();
		}
		handler.onStop();
	}

}
