package bytecrawl.evtj.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Gateway implements Runnable {

	EvtJServer		server;
	ServerSocket 	server_socket;
	
	public Gateway(EvtJServer server) {
		this.server		= server;
		server_socket	= server.getServerSocket();
	}
	
	public void run()
	{
		while(server.initialising()) {};
		while(server.isActive())
		{
			if(server.isPaused()) {
				System.out.println("Server paused");
				while(server.isPaused()) {};
				System.out.println("Server resumed");
			}
			try
			{
				server.addSocketToBook(server_socket.accept());
				System.out.println("New client accepted		[accepted clients] = "+server.getAcceptedClients());
			}catch(IOException e){
				//System.out.println("Error accepting connection.");
			}
		}
	}
}