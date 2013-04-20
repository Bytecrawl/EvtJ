package bytecrawl.evtj.example;

import bytecrawl.evtj.SocketEngine;
import bytecrawl.evtj.server.EvtJServer;

public class Main {
	
	private static int PORT = 4444;
	
	public static void main(String[] args) throws InterruptedException
	{	
		EvtJServer server = new EvtJServer(PORT);
		server.start();
		


		SocketEngine se1 = new SocketEngine("127.0.0.1", PORT);
		SocketEngine se2 = new SocketEngine("127.0.0.1", PORT);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String re = "";
		
		se1.send("slow");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		se1.send("fast");
		se2.send("fast");
		

		re = se2.recv();
		System.out.println("[Client] : "+re);
		re = se1.recv();
		System.out.println("[Client] : "+re);
		
		re = se1.recv();
		System.out.println("[Client] : "+re);
		
		se1.close();
		se2.close();

		while(true) {try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		
	}
}
