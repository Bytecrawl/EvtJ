package bytecrawl.evtj.server.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.SocketBook;

public class EventHandler implements Handler {
	
	private EvtJServer server;
	private Socket s;
	private SocketBook book;
	private BufferedReader br;
	private BufferedWriter bw;
	
	public EventHandler(EvtJServer server) {
		this.server = server;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRun() {
		book = server.getSocketBook();
		for(int i=0; i<book.size(); i++)
		{
			try {
				s = book.get(i);
				br = new BufferedReader( 
						new InputStreamReader(s.getInputStream())
					);
				bw = new BufferedWriter(
						new OutputStreamWriter(s.getOutputStream())
					);
				if(br.ready())
					ProtocolHandler.run(server, br, bw);
			}catch(IOException e){
				System.out.println("IOException on EventHandler");
			}
		}
	}
}