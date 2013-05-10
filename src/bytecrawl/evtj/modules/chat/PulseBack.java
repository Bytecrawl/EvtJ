package bytecrawl.evtj.modules.chat;

public class PulseBack extends Response {

	@SuppressWarnings("unused")
	private String content = "PulseBack\n";
	
	public PulseBack() {
		this.setType(Response.RBEAT_TYPE);
	}
	

}
