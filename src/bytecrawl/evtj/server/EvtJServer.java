package bytecrawl.evtj.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import bytecrawl.evtj.server.handlers.Dispatcher;
import bytecrawl.evtj.server.handlers.Worker;
import bytecrawl.evtj.utils.EvtJClient;
import bytecrawl.evtj.utils.EvtJRequest;

import org.apache.log4j.Logger;

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
	
	private int PORT;
	
	private Logger logger = Logger.getLogger("app");

	public EvtJServer(int port, EvtJModule module)
	{
		connected_clients = 0;
		this.PORT = port;
		this.module = module;
	}
	
	public synchronized int getConnectedClients() { return connected_clients; }
	
	public synchronized EvtJModule getModule() { return module; }
	
	public synchronized Selector getSelector() { return selector; }
	
	public int getServedRequests() { return served_requests; }
	
	public int getWorkerPoolSize() { return worker_pool_size; }

	private void initialize_channel(int port) throws IOException
	{
		server_channel = ServerSocketChannel.open();
		server_channel.configureBlocking(false);
		server_channel.socket().bind(new InetSocketAddress(port));
		server_channel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public synchronized boolean isActive() { return active; }
		
	public synchronized boolean isInitialising() { return initialising; }
	
	public synchronized boolean isPaused() { return paused; }
	
	public synchronized void newAcceptedClient(EvtJClient client) {
		connected_clients++;
		logger.info("Connection accepted from "+client.getIP()+
				" [ "+connected_clients+" online clients ]");
	}
	
	public synchronized void newDisconnectedClient(EvtJClient client) { 
		connected_clients--;
		logger.info("Disconnection from "+client.getIP());
	}
	
	public synchronized void newServedRequest() { served_requests++; }
	
	private void open_selector() throws IOException
	{
		selector = Selector.open();
	}
	
	public synchronized void pause() { pause_module(); paused = true; }

	/**
	 * Queue a request to the thread pool of EvtJServer
	 * by passing a custom module worker for said request.
	 */
	public void queue(EvtJRequest request)
	{
		Worker handler = (Worker)worker_executor.getHandler();
		EvtJModuleWorker worker = module.getWorker();
		worker.setEvtJRequest(request);
		handler.pushTask(worker);
	}
	
	public synchronized void resume() { resume_module(); paused = false; }

	public void start()
	{
		initialising = true;
		try
		{
			open_selector();
			initialize_channel(PORT);
		}catch(IOException e){
			logger.error("EvtJServer could not bind the port "+PORT, e);
			System.exit(1);
		}finally{
			start_module();
			start_executors();
			paused = false;
			active = true;
			initialising = false;
			logger.info("Server started\n");
		}
	}
	
	private void start_executors()
	{
		module.onStart();
		
		worker_executor = new EvtJExecutor(this, new Worker(this));
		worker_executor.start();
		
		dispatcher_executor = new EvtJExecutor(this, new Dispatcher(this));
		dispatcher_executor.start();
		
		while(!worker_executor.isAlive()) {}
		while(!dispatcher_executor.isAlive()) {}
	}

	public void stop()
	{
		if(active==false && initialising == false) {
			logger.warn("Server is not running.");
			return;
		}

		paused = false;
		active = false;

		stop_module();
		stop_executors();
		
		try {
			server_channel.close();
		} catch (IOException e) {
			logger.error("Unknown error closing ServerSocketChannel", e);
		}
		
		logger.info("Server stopped.");
	}
	
	private void stop_executors()
	{
		dispatcher_executor.interrupt();
		worker_executor.interrupt();
		
		while(!worker_executor.isAlive()) {}
		while(!dispatcher_executor.isAlive()) {}
	}
	
	private void start_module() {
		module.onStart();
	}
	
	private void stop_module() {
		module.onStop();
	}
	
	private void pause_module() {
		module.onPause();
	}
	
	private void resume_module() {
		module.onResume();
	}
}
