package bytecrawl.evtj.server.handlers;

import java.io.IOException;
import java.net.ServerSocket;

import bytecrawl.evtj.server.EvtJServer;

public class GatewayHandler implements Handler {

	EvtJServer server;
	ServerSocket server_socket;
	
	public GatewayHandler(EvtJServer server) {
		this.server = server;
		server_socket = server.getServerSocket();
	}
	
	@Override
	public void onPause() {
		System.out.println("Server paused");
	}

	@Override
	public void onResume() {
		System.out.println("Server resumed");
	}

	@Override
	public void onStop() {

	}

	@Override
	public void onRun() {
		try
		{
			server.addSocketToBook(server_socket.accept());
			System.out.println("New client accepted		[connected clients] = "+server.getConnectedClients());
		}catch(IOException e){
			System.out.println("Error accepting connection.");
		}
	}
}