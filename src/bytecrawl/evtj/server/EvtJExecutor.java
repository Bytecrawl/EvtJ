package bytecrawl.evtj.server;

import bytecrawl.evtj.server.handlers.HandlerI;

public class EvtJExecutor extends Thread implements Runnable {
	
	private EvtJServer server;
	private HandlerI handler;
	
	public EvtJExecutor(EvtJServer server, HandlerI handler)
	{
		this.server = server;
		this.handler = handler;
	}

	public HandlerI getHandler() { return handler; }

	private void sleep() {
		try {
			Thread.sleep(0, 500);
		} catch (InterruptedException e) {
			this.interrupt();
		}
	}
	
	public void terminate() {
		this.interrupt();
	}
	
	public void run()
	{
		while(!Thread.currentThread().isInterrupted()) {
			while(server.isInitialising()) { sleep(); }
			while(server.isActive())
			{
				if(server.isPaused()) {
					handler.onPause();
					while(server.isPaused()) { sleep(); }
					handler.onResume();
				}
				handler.onRun();
				sleep();
			}
			handler.onStop();
		}
	}

}
