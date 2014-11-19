package bytecrawl.evtj.server;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.executors.ExecutionPool;
import bytecrawl.evtj.executors.ThreadedExecutor;
import bytecrawl.evtj.server.modules.Module;
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
    private ThreadedExecutor dispatcherExecutor;
    private ThreadedExecutor workerExecutor;
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

    public boolean isInitialising() {
        return state.isInitialising();
    }

    public synchronized void newAcceptedConnection(Client client) {
        clientsConnected++;
        logger.info("Connection accepted from " + client.getAddress() +
                " [ " + clientsConnected + " online clients ]");
    }

    public synchronized void newDisconnection() {
        clientsConnected--;
        logger.info("Disconnection");
    }

    public synchronized void newServedRequest() {
        clientsConnected++;
    }

    private void selectorOpen() throws IOException {
        selector = Selector.open();
    }

    public synchronized void pause() {
        if (state.isPaused()) return;

        modulePause();
        executorsPause();

        state.paused();

        logger.info("Server paused");
    }

    /**
     * Queue a request to the thread pool of EvtJServer
     * by passing a custom module worker for said request.
     */
    public synchronized void queue(Request request) {
        ExecutionPool handler = (ExecutionPool) workerExecutor.getExecutable();
        Module worker;
        try {
            worker = module.getWorker();
            worker.setRequest(request);
            handler.pushTask(worker);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void resume() {
        if (!state.isPaused()) {
            logger.warn("Server is stopped, can't resume");
            return;
        }

        moduleResume();
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
            moduleStart();
            executorsStart();

            state.started();

            logger.info("Server started");

        } catch (IOException e) {
            logger.error("EvtJServer could not bind the port " + port, e);
            System.exit(1);
        }
    }

    private void executorsStart() {
        workerExecutor = new ThreadedExecutor(this);
        dispatcherExecutor = new ThreadedExecutor(this);

        ExecutionPool executionPool = new ExecutionPool(this);
        RequestDispatcher requestDispatcher = new RequestDispatcher(this);

        workerExecutor.setExecutable(executionPool);
        dispatcherExecutor.setExecutable(requestDispatcher);

        workerExecutor.start();
        dispatcherExecutor.start();

        while (!workerExecutor.isAlive()) {
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

        moduleStop();
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
        workerExecutor.interrupt();
        while (dispatcherExecutor.isAlive()) {
        }
        while (workerExecutor.isAlive()) {
        }
    }

    private void executorsPause() {
        dispatcherExecutor.pause();
        workerExecutor.pause();
    }

    private void executorsResume() {
        dispatcherExecutor.unpause();
        workerExecutor.unpause();
    }

    private void moduleStart() {
        module.onStart();
    }

    private void moduleStop() {
        module.onStop();
    }

    private void modulePause() {
        module.onPause();
    }

    private void moduleResume() {
        module.onResume();
    }

    public boolean isActive() {
        return state.isActive();
    }

    public boolean isPaused() {
        return state.isPaused();
    }

}
