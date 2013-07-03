package bytecrawl.evtj.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bytecrawl.evtj.server.handlers.Dispatcher;
import bytecrawl.evtj.server.handlers.Worker;
import bytecrawl.evtj.utils.EvtJClient;
import bytecrawl.evtj.utils.EvtJRequest;


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
	
	private Logger logger = LoggerFactory.getLogger("EvtJServer");

	public EvtJServer(int port, EvtJModule module)
	{
		connected_clients = 0;
		this.PORT = port;
		this.module = module;
        this.paused = false;
        this.active = false;
        this.initialising = false;
	}
	
	public synchronized int getConnectedClients() { return connected_clients; }
	
	public synchronized EvtJModule getModule() { return module; }
	
	public synchronized Selector getSelector() { return selector; }
	
	public synchronized int getServedRequests() { return served_requests; }
	
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

	public synchronized void newAcceptedConnection(EvtJClient client) {
		connected_clients++;
		logger.info("Connection accepted from "+client.getIP()+
				" [ "+connected_clients+" online clients ]");
	}
	
	public synchronized void newDisconnection() { 
		connected_clients--;
        logger.info("Disconnection");
	}
	
	public synchronized void newServedRequest() { served_requests++; }
	
	private void open_selector() throws IOException
	{
		selector = Selector.open();
	}
	
	public synchronized void pause() {
        if(paused) return;

        pause_module();
        paused = true;
    }

	/**
	 * Queue a request to the thread pool of EvtJServer
	 * by passing a custom module worker for said request.
	 */
	public synchronized void queue(EvtJRequest request)
	{
		Worker handler = (Worker)worker_executor.getHandler();
		EvtJModuleWorker worker = module.getWorker();
		worker.setEvtJRequest(request);
		handler.pushTask(worker);
	}
	
	public synchronized void resume() {
        if(!isPaused()) {
            logger.warn("Can't resume a stopped server");
            return;
        }

        resume_module();

        paused = false;
    }

	public void start()
	{
        // Fail silently if the server is already started.
        if(isActive()) {
            logger.warn("Server is already running");
            return;
        }

		initialising = true;
		try
		{
			open_selector();
			initialize_channel(PORT);
            start_module();
            start_executors();
            paused = false;
            initialising = false;
            active = true;
            logger.info("Server started");
		}catch(IOException e){
			logger.error("EvtJServer could not bind the port "+PORT, e);
			System.exit(1);
		}
	}
	
	private void start_executors()
	{
		worker_executor = new EvtJExecutor(this, new Worker(this));
		worker_executor.start();
		
		dispatcher_executor = new EvtJExecutor(this, new Dispatcher(this));
		dispatcher_executor.start();
		
		while(!worker_executor.isAlive()) {}
		while(!dispatcher_executor.isAlive()) {}
	}

	public void stop()
	{
		if(!isActive()) {
			logger.warn("Server is already stopped");
			return;
		}

		stop_module();

        initialising = false;
		paused = false;
		active = false;

		stop_executors();
		
		try {
			server_channel.close();
            server_channel = null;
		} catch (IOException e) {
			logger.error("Unknown error closing ServerSocketChannel", e);
		}

        try {
            Thread.sleep(100);
        }catch(InterruptedException e) {

        }
		
		logger.info("Server stopped");
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
