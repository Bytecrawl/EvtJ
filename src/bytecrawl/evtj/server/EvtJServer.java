package bytecrawl.evtj.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import bytecrawl.evtj.NetEvent;

public class EvtJServer {

	private boolean 		active;
	private boolean			paused;
	private boolean			initialising;
	private int				accepted_clients;
	private ServerSocket	server_socket;
	private List<Socket> 	socket_book;
	private List<NetEvent>	events_book;
	private Thread 			gateway_thread;
	private Thread			handler_thread;
	
	private int port;

	public EvtJServer(int port)
	{
		try
		{
			server_socket 	= new ServerSocket(port);
			socket_book 	= new ArrayList<Socket>();
			events_book 	= new ArrayList<NetEvent>();
		}catch(IOException e){
			System.out.println("EvtJServer could not bind the port "+port);
			System.exit(1);
		}finally{
			this.port = port;
			accepted_clients = 0;
		}
	}
	
	public synchronized boolean isActive()		{ return active; }
	public synchronized boolean isPaused()		{ return paused; }
	public synchronized boolean initialising()	{ return initialising; }
	
	public int			getAcceptedClients()	{ return accepted_clients; }
	public ServerSocket getServerSocket() 		{ return server_socket; }
	public List<Socket>	getSocketBook() 		{ return socket_book; }

	public void pause() 						{ paused = true; }
	public void resume() 						{ paused = false; }
	
	public void addSocketToBook(Socket s)
	{
		socket_book.add(s);
		accepted_clients++;
	}
	
	public void start_handler()
	{
		Handler handler = new Handler(this);
		handler_thread = new Thread(handler);
		handler_thread.start();
		while(!handler_thread.isAlive()) {};
	}
	
	public void start_gateway()
	{
		Gateway gw = new Gateway(this);
		gateway_thread = new Thread(gw);
		gateway_thread.start();
		while(!gateway_thread.isAlive()) {};
	}

	public void start()
	{
		initialising 	= true;
		start_gateway();
		start_handler();
		paused 			= false;
		active 			= true;
		initialising 	= false;
	}
	
	public void stop()
	{
		paused = false;
		active = false;
		handler_thread.interrupt();
		gateway_thread.interrupt();
		try {
			server_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(gateway_thread.isAlive()) {};
		while(handler_thread.isAlive()) {};
		System.out.println("Server stopped\n");
	}
	
}
