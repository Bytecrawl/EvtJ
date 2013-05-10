package bytecrawl.evtj.modules.chat;

public class Pulse extends Response {
	
	@SuppressWarnings("unused")
	private String content = "Pulse\n";

	public Pulse() {
		this.setType(Response.BEAT_TYPE);
	}
	
}
