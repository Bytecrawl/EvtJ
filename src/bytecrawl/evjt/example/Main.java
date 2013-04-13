package bytecrawl.evjt.example;

import bytecrawl.evtj.SocketEngine;
import bytecrawl.evtj.server.EvtJServer;

public class Main {
	
	private static int PORT = 4449;
	
	public static void main(String[] args)
	{
		EvtJServer server = new EvtJServer(PORT);
		server.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SocketEngine se1 = new SocketEngine("127.0.0.1", PORT);
		SocketEngine se2 = new SocketEngine("127.0.0.1", PORT);
		server.pause();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SocketEngine se3 = new SocketEngine("127.0.0.1", PORT);
		server.resume();
		SocketEngine se4 = new SocketEngine("127.0.0.1", PORT);
		
		/*
		 * 	
		 */
		String rcv = se3.recv();
		System.out.printf("Recibido :'%s'\n", rcv);
		server.stop();
	}
}
