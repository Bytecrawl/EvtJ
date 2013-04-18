package bytecrawl.evtj.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import bytecrawl.evtj.protocols.Protocol;
import bytecrawl.evtj.server.handlers.DispatcherHandler;
import bytecrawl.evtj.server.handlers.WorkerHandler;


public class EvtJServer {
	
	private boolean active;
	private boolean	paused;
	private boolean	initialising;
	private int connected_clients;
	
	private ServerSocketChannel server_channel;
    private Selector selector;

	private EvtJExecutor dispatcher_executor;
	private EvtJExecutor worker_executor;
	
	public EvtJServer(int port)
	{
		try
		{
			selector = Selector.open();
			server_channel = ServerSocketChannel.open();
			server_channel.configureBlocking(false);
			server_channel.socket().bind(new InetSocketAddress(port));
			server_channel.register(selector, SelectionKey.OP_ACCEPT);
		}catch(IOException e){
			System.out.println("EvtJServer could not bind the port "+port);
			System.exit(1);
		}finally{
			connected_clients = 0;
		}
	}
	
	public synchronized boolean isActive() { return active; }
	public synchronized boolean isPaused() { return paused; }
	public synchronized boolean isInitialising() { return initialising; }
	
	public synchronized int getConnectedClients() { return connected_clients; }
	public synchronized Selector getSelector() { return selector; }

	public synchronized void pause() { paused = true; }
	public synchronized void resume() { paused = false; }

	public void queueRequest(SocketChannel channel, String cmd)
	{
		WorkerHandler handler = (WorkerHandler)worker_executor.getHandler();
		Protocol prot = new Protocol(channel, cmd);
		handler.pushTask(prot);
	}

	public void start_dispatcher()
	{
		DispatcherHandler handler = new DispatcherHandler(this);
		dispatcher_executor = new EvtJExecutor(this, handler);
		dispatcher_executor.start();
		while(!dispatcher_executor.isAlive()) {}
	}
	
	public void start_worker()
	{
		WorkerHandler handler = new WorkerHandler(this);
		worker_executor = new EvtJExecutor(this, handler);
		worker_executor.start();
		while(!worker_executor.isAlive()) {}
	}
	
	public void start()
	{
		initialising = true;
		start_worker();
		start_dispatcher();
		paused = false;
		active = true;
		initialising = false;
	}
	
	public void stop()
	{
		paused = false;
		active = false;

		dispatcher_executor.interrupt();
		
		try {
			server_channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(dispatcher_executor.isAlive()) {}
		
		System.out.println("Server stopped\n");
	}
	
}
