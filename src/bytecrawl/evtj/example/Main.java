package bytecrawl.evtj.example;

import bytecrawl.evtj.modules.chat.ChatModule;
import bytecrawl.evtj.server.EvtJServer;

public class Main {
	
	private static int PORT = 4444;
	
	public static void main(String[] args) throws InterruptedException
	{	
		ChatModule module = new ChatModule();
		EvtJServer server = new EvtJServer(PORT, module);
		
		server.start();

		Client se1 = new Client("Client 1", "Client 2", "127.0.0.1", PORT);
		Client se2 = new Client("Client 2", "Client 1", "127.0.0.1", PORT);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		se1.start();
		se2.start();

		//server.stop();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Served requests: "+server.getServedRequests());
	}
}
