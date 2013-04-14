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

	private void s100ms() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(!Thread.currentThread().isInterrupted()) {
			while(server.isInitialising()) { s100ms(); }
			while(server.isActive())
			{
				if(server.isPaused()) {
					handler.onPause();
					while(server.isPaused()) { s100ms(); }
					handler.onResume();
				}
				handler.onRun();
			}
			handler.onStop();
		}
	}

}
