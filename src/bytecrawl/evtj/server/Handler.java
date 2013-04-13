package bytecrawl.evtj.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class Handler implements Runnable {
	
	private EvtJServer		server;
	private Socket 			s;
	private List<Socket>	book;
	
	public Handler(EvtJServer server) {
		this.server = server;
	}
	
	public void run()
	{
		while(!Thread.currentThread().isInterrupted()) {
			while(server.isInitialising()) {};
			while(server.isActive() && !Thread.currentThread().isInterrupted())
			{
				while(server.isPaused()) {};
				
				book = server.getSocketBook();
				for(int i=0; i<server.getAcceptedClients(); i++)
				{
						
					s = book.get(i);
					try{
						BufferedWriter bw = new BufferedWriter( 
							new OutputStreamWriter(s.getOutputStream())
						);
						bw.write("AJU NANU\n");
						bw.flush();
					}catch(IOException e){
					
					}
		
				}
			}
		}
	}
}