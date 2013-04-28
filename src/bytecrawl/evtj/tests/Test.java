package bytecrawl.evtj.tests;

public class Test {
	
	public void sleep() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void ServerInit() {
		
		/*
		EvtJServer server = new EvtJServer(4444);
		server.start();
		
		//SocketEngine se1 = new SocketEngine("127.0.0.1", 4444);
		sleep();
		assertEquals("Server initialized", server.getConnectedClients(), 1);
		
		//se1.close();
		sleep();
		
		assertEquals("Disconnection", server.getConnectedClients(), 0);
		*/
	}

}
