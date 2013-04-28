package bytecrawl.evtj.modules.chat;

public class Register extends Response {

	private String name;

	public Register() {

	}
	
	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
}
