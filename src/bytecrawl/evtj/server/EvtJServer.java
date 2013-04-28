package bytecrawl.evtj.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import bytecrawl.evtj.server.handlers.DispatcherHandler;
import bytecrawl.evtj.server.handlers.WorkerHandler;
import bytecrawl.evtj.utils.EvtJClient;


public class EvtJServer {
	
	final private int worker_pool_size = 5;
	
	private boolean active;
	private boolean	paused;
	private boolean	initialising;
	private int connected_clients;
	private int served_requests;
	
	private ServerSocketChannel server_channel;
    private Selector selector;

	private EvtJExecutor dispatcher_executor;
	private EvtJExecutor worker_executor;
	private EvtJModule module;
	
	public EvtJServer(int port, EvtJModule module)
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
			this.module = module;
		}
	}
	
	public synchronized void newAcceptedClient(EvtJClient client) {
		connected_clients++;
		try {
			System.out.println("Connection accepted from "+client.getChannel().getLocalAddress().toString()+" [ "+connected_clients+" online clients ]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void newDisconnectedClient(EvtJClient client) { 
		connected_clients--;
		try {
			System.out.println("Disconnection from "+client.getChannel().getLocalAddress().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void queueRequest(EvtJClient client, String cmd)
	{
		WorkerHandler handler = (WorkerHandler)worker_executor.getHandler();
		EvtJModuleWorkerI worker = module.getWorker();
		worker.set(client, cmd);
		handler.pushTask(worker);
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
		worker_executor.interrupt();
		
		try {
			server_channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(dispatcher_executor.isAlive()) {}
		while(worker_executor.isAlive()) {}
		
		System.out.println("Server stopped\n");
	}
	
	public synchronized boolean isActive() { return active; }
	public synchronized boolean isPaused() { return paused; }
	public synchronized boolean isInitialising() { return initialising; }
	
	public synchronized int getConnectedClients() { return connected_clients; }
	public synchronized Selector getSelector() { return selector; }

	public synchronized void pause() { paused = true; }
	public synchronized void resume() { paused = false; }

	public synchronized EvtJModule getModule() { return module; }
	public int getWorkerPoolSize() { return worker_pool_size; }
	
	public void setServedRequests(int r) { served_requests = r; }
	public int getServedRequests() { return served_requests; }
	
}
