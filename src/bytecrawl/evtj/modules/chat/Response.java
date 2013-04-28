package bytecrawl.evtj.modules.chat;

public class Response {

	public final static int BEAT_TYPE = 1;
	public final static int RBEAT_TYPE = 2;
	public final static int MESSAGE_TYPE = 3;
	public final static int REGISTER_TYPE = 4;
	
	private int type;

	public Response() {
		
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public int getType() { return type; }
	

}
