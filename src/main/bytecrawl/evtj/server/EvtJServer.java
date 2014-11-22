package bytecrawl.evtj.server;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.executors.ExecutionPool;
import bytecrawl.evtj.executors.ExecutionThread;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.modules.ModuleRunnable;
import bytecrawl.evtj.server.requests.Client;
import bytecrawl.evtj.server.requests.Request;
import bytecrawl.evtj.server.requests.RequestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class EvtJServer {

    private int clientsConnected;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ExecutionThread dispatcherExecutor;
    private ExecutionThread workerPoolExecutor;
    private Module module;
    private int port;
    private Logger logger = LoggerFactory.getLogger("EvtJServer");
    private State state;

    public EvtJServer(int port, Module module) {
        this.clientsConnected = 0;
        this.port = port;
        this.module = module;
        this.state = new State();
        Configuration.newConfiguration();
    }

    public EvtJServer(int port, Module module, String configurationPath) throws ConfigurationException {
        this.clientsConnected = 0;
        this.port = port;
        this.module = module;
        this.state = new State();
        Configuration.newConfiguration(configurationPath);
    }

    public synchronized State getState() {
        return state;
    }

    public synchronized int getConnectedClients() {
        return clientsConnected;
    }

    public synchronized Module getModule() {
        return module;
    }

    public synchronized Selector getSelector() {
        return selector;
    }

    private void channelInitialize(int port) throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public synchronized void newAcceptedConnection(Client client) {
        clientsConnected++;
        logger.info("Connection accepted from " + client.getAddress() +
                " [ " + clientsConnected + " online clients ]");
    }

    public synchronized void newDisconnection(Client client) {
        clientsConnected--;
        logger.info("Disconnection from " + client.getAddress());
    }

    public synchronized void newServedRequest() {
        clientsConnected++;
    }

    private void selectorOpen() throws IOException {
        selector = Selector.open();
    }

    public synchronized void pause() {
        if (state.isPaused()) return;

        module.onPause();
        executorsPause();

        state.paused();

        logger.info("Server paused");
    }

    /**
     * Queue a request to the thread pool of EvtJServer
     * by passing a custom module worker for said request.
     */
    public synchronized void queue(Request request) {
        ExecutionPool pool = (ExecutionPool) workerPoolExecutor.getExecutable();
        ModuleRunnable runnable = new ModuleRunnable(module, request);
        pool.pushTask(runnable);
    }

    public void resume() {
        if (!state.isPaused()) {
            logger.warn("Server is stopped, can't resume");
            return;
        }

        module.onResume();
        executorsResume();

        state.resumed();
    }

    public void start() {
        if (state.isActive()) {
            logger.warn("Server is already running");
            return;
        }

        state.initialised();

        try {

            selectorOpen();
            channelInitialize(port);
            module.onStart();
            executorsStart();

            state.started();

            logger.info("Server started");

        } catch (IOException e) {
            logger.error("EvtJServer could not bind the port " + port, e);
            System.exit(1);
        }
    }

    private void executorsStart() {
        ExecutionPool executionPool = new ExecutionPool();
        RequestDispatcher requestDispatcher = new RequestDispatcher(this);

        workerPoolExecutor = new ExecutionThread(state, executionPool);
        dispatcherExecutor = new ExecutionThread(state, requestDispatcher);

        workerPoolExecutor.start();
        dispatcherExecutor.start();

        while (!workerPoolExecutor.isAlive()) {
        }
        while (!dispatcherExecutor.isAlive()) {
        }
    }

    public void stop() {
        if (!state.isActive()) {
            logger.warn("Server is already stopped");
            return;
        }

        state.stopped();

        module.onStop();
        executorsStop();

        try {
            serverChannel.close();
        } catch (IOException e) {
            logger.error("Unknown error closing ServerSocketChannel", e);
        }

        logger.info("Server stopped");
    }

    private void executorsStop() {
        dispatcherExecutor.interrupt();
        workerPoolExecutor.interrupt();
        while (dispatcherExecutor.isAlive()) {
        }
        while (workerPoolExecutor.isAlive()) {
        }
    }

    private void executorsPause() {
        dispatcherExecutor.pause();
        workerPoolExecutor.pause();
    }

    private void executorsResume() {
        dispatcherExecutor.unpause();
        workerPoolExecutor.unpause();
    }

}
