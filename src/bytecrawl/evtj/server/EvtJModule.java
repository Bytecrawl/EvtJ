package bytecrawl.evtj.server;

import bytecrawl.evtj.utils.EvtJClient;

public abstract class EvtJModule implements EvtJModuleI {

	private EvtJClient client;
	private String request;
	
	@SuppressWarnings("unused")
	private void setClient(EvtJClient client)
	{
		this.client = client;
	}
	
	@SuppressWarnings("unused")
	private void setCommand(String request)
	{
		this.request = request;
	}
	
	@SuppressWarnings("unused")
	private EvtJClient getClient() { return client; }
	@SuppressWarnings("unused")
	private String getRequest() { return request; }
	
}
