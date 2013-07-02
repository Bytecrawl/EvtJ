package bytecrawl.evtj.utils;

public class EvtJRequest {

	private EvtJClient client;
	private String request;
	
	public EvtJRequest(EvtJClient client, String request) {
		this.client = client;
		this.request = request;
	}
	
	public String getRequest() {
		return request;
	}
	
	public EvtJClient getClient() {
		return client;
	}
}
