package bytecrawl.evtj.example;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJClient;

public class Main {
	
	private static int PORT = 4444;
	
	public static void main(String[] args) throws InterruptedException
	{	
		EvtJServer server = new EvtJServer(PORT);
		server.start();

		EvtJClient se1 = new EvtJClient("Client 1", "127.0.0.1", PORT);
		EvtJClient se2 = new EvtJClient("Client 2", "127.0.0.1", PORT);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		se1.start();
		se2.start();
		

		while(true) {try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		
	}
}
