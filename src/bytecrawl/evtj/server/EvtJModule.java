package bytecrawl.evtj.server;

import bytecrawl.evtj.utils.EvtJClient;

public abstract class EvtJModule {

	private EvtJClient client;
	private String command;
	private Class<Runnable> worker_type;
	
	public void setClient(EvtJClient client)
	{
		this.client = client;
	}
	
	public void setCommand(String command)
	{
		this.command = command;
	}
	
	public EvtJModuleWorkerI getWorker() { return null; }
	
}
