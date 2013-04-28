package bytecrawl.evtj.modules.chat;

public class Message extends Response {

	private String content;
	private String from, to;

	
	public Message(String from, String to, String content)
	{
		this.content = content;
		this.from = from;
		this.to = to;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public String getFrom()
	{
		return from;
	}
	
	public String getTo()
	{
		return to;
	}
	
}
