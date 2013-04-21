package bytecrawl.evtj.server;

import bytecrawl.evtj.server.handlers.Handler;

public class EvtJExecutor extends Thread implements Runnable {
	
	private EvtJServer server;
	private Handler handler;
	
	public EvtJExecutor(EvtJServer server, Handler handler)
	{
		this.server = server;
		this.handler = handler;
	}

	public Handler getHandler() { return handler; }

	private void sleep() {
		try {
			Thread.sleep(0, 500);
		} catch (InterruptedException e) {
			this.interrupt();
		}
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
