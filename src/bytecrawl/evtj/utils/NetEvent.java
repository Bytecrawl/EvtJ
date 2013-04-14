package bytecrawl.evtj.utils;

import java.net.Socket;

public class NetEvent {
	
	private Socket sck;
	private String cmd;
	
	public NetEvent(Socket sck, String cmd) {
		this.sck = sck;
		this.cmd = cmd;
	}

}
