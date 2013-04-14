package bytecrawl.evjt.example;

import bytecrawl.evtj.SocketEngine;
import bytecrawl.evtj.server.EvtJServer;

public class Main {
	
	private static int PORT = 4444;
	
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
		//server.pause();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SocketEngine se3 = new SocketEngine("127.0.0.1", PORT);

		SocketEngine se4 = new SocketEngine("127.0.0.1", PORT);
		
		String rcv;
		
		/*
		se1.send("GET /users/online HTTP/1.1");
		se2.send("GET /users/online HTTP/1.1");
		se3.send("GET /users/online HTTP/1.1");
		se4.send("GET /users/online HTTP/1.1");
		se3.send("GET /users/online HTTP/1.1");
		se4.send("GET /users/online HTTP/1.1");
		se3.send("GET /users/online HTTP/1.1");
		se4.send("GET /users/online HTTP/1.1");
		se3.send("GET /users/online HTTP/1.1");
		se4.send("GET /users/online HTTP/1.1");
		*/
		
		se1.send("hola k ase");
		se2.send("ola");
		se3.send("asdios");
		se4.send("as");
		
		
		for(int i=0; i<2; i++)
		{
			rcv = se1.recv();
			System.out.printf("Se1 -> '%s'\n", rcv);
			rcv = se2.recv();
			System.out.printf("Se2 -> '%s'\n", rcv);
			rcv = se3.recv();
			System.out.printf("Se3 -> '%s'\n", rcv);
			rcv = se4.recv();
			System.out.printf("Se4 -> '%s'\n", rcv);
			se1.send("hola k ase");
			se2.send("ola");
			se3.send("asdios");
			se4.send("as");
			if(i==0) se3.close();
		}
		
		se1.close();
		se2.close();

		server.stop();
	}
}
