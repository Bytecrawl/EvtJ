package bytecrawl.evtj.server;

import bytecrawl.evtj.utils.EvtJClient;

public abstract class EvtJModuleWorker implements Runnable {

	protected EvtJClient client;
	protected String command;
	
	public EvtJModuleWorker()
	{
		
	}
	
	public void setEvtJClient(EvtJClient client) { this.client = client; }
	public void setCommand(String command) { this.command = command; }
}
