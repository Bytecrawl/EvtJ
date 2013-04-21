package bytecrawl.evtj.protocols.evtj;

import java.util.ArrayList;
import java.util.Collection;

public class Response {

	public final static int BEAT_TYPE = 1;
	public final static int RBEAT_TYPE = 2;
	
	private String source;
	private String destination;
	private int type;
	public Collection<Object> payload = new ArrayList<Object>();
	
	public Response(String source, String destination)
	{
		this.source = source;
		this.destination = destination;
	}
	public Response(String source, String destination, int type)
	{
		this.source = source;
		this.destination = destination;
		this.type = type;
	}
	
	public Response addPayload(Object payload)
	{
		this.payload.add(payload);
		return this;
	}
	
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public String getSource() { return source; }
	
	public String getDestination() { return destination; }
	
	public int getType() { return type; }
	
	public Object getPayload() { return payload; }
	

}
