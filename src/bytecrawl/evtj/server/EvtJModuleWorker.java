package bytecrawl.evtj.server;

import bytecrawl.evtj.utils.EvtJClient;

public abstract class EvtJModuleWorker {

	protected EvtJClient client;
	protected String command;
	
	public void set(EvtJClient client, String command)
	{
		this.client = client;
		this.command = command;
	}
	
}
