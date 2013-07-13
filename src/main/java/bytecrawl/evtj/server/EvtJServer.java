package bytecrawl.evtj.server;

import bytecrawl.evtj.server.executors.EvtJExecutor;
import bytecrawl.evtj.server.handlers.Dispatcher;
import bytecrawl.evtj.server.handlers.Worker;
import bytecrawl.evtj.server.modules.EvtJModule;
import bytecrawl.evtj.server.modules.EvtJModuleWorker;
import bytecrawl.evtj.utils.EvtJClient;
import bytecrawl.evtj.utils.EvtJConfiguration;
import bytecrawl.evtj.utils.EvtJRequest;
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
    private EvtJExecutor dispatcherExecutor;
    private EvtJExecutor workerExecutor;
    private EvtJModule module;
    private int PORT;
    private Logger logger = LoggerFactory.getLogger("EvtJServer");
    private EvtJState state;

    public EvtJServer(int port, EvtJModule module) {
        this.clientsConnected = 0;
        this.PORT = port;
        this.module = module;
        this.state = new EvtJState();
        EvtJConfiguration.newConfiguration();
    }

    public EvtJServer(int port, EvtJModule module, String configurationPath) {
        this.clientsConnected = 0;
        this.PORT = port;
        this.module = module;
        this.state = new EvtJState();
        EvtJConfiguration.newConfiguration(configurationPath);
    }

    public synchronized EvtJState getState() {
        return state;
    }

    public synchronized int getConnectedClients() {
        return clientsConnected;
    }

    public synchronized EvtJModule getModule() {
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

    public synchronized void newAcceptedConnection(EvtJClient client) {
        clientsConnected++;
        logger.info("Connection accepted from " + client.getIP() +
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
    public synchronized void queue(EvtJRequest request) {
        Worker handler = (Worker) workerExecutor.getHandler();
        EvtJModuleWorker worker = module.getWorker();
        worker.setEvtJRequest(request);
        handler.pushTask(worker);
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
            channelInitialize(PORT);
            moduleStart();
            executorsStart();

            state.started();

            logger.info("Server started");

        } catch (IOException e) {
            logger.error("EvtJServer could not bind the port " + PORT, e);
            System.exit(1);
        }
    }

    private void executorsStart() {
        workerExecutor = new EvtJExecutor(this);
        dispatcherExecutor = new EvtJExecutor(this);

        Worker worker = new Worker(this);
        Dispatcher dispatcher = new Dispatcher(this);

        workerExecutor.setHandler(worker);
        dispatcherExecutor.setHandler(dispatcher);

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
