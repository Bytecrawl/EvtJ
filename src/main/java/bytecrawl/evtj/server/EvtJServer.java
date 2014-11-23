package bytecrawl.evtj.server;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.executors.ExecutionPool;
import bytecrawl.evtj.executors.ExecutionThread;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.modules.ModuleRunnable;
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

    private Logger logger = LoggerFactory.getLogger("EvtJServer");

    private int port;

    private ServerSocketChannel serverChannel;
    private Selector selector;

    private Module module;
    private State state;

    /** Threaded executors, and executables */
    private ExecutionThread dispatcherExecutor;
    private ExecutionThread workerPoolExecutor;
    private RequestDispatcher requestDispatcher;
    private ExecutionPool executionPool;

    public EvtJServer(int port, Module module) {
        this.port = port;
        this.module = module;
        this.state = new State();
        Configuration.newConfiguration();
        executionPool = new ExecutionPool();
        requestDispatcher = new RequestDispatcher(this);
    }

    public EvtJServer(int port, Module module, String configurationPath) throws ConfigurationException {
        this.port = port;
        this.module = module;
        this.state = new State();
        Configuration.newConfiguration(configurationPath);
        executionPool = new ExecutionPool();
        requestDispatcher = new RequestDispatcher(this);
    }

    public State getState() {
        return state;
    }

    public synchronized Module getModule() {
        return module;
    }

    public Selector getSelector() {
        return selector;
    }

    private void channelInitialize(int port) throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public synchronized void pause() {
        if (state.isPaused()) return;
        state.paused();
        module.onPause();
        pauseExecutors();
        logger.info("Server paused");
    }

    /**
     * Queue a request to the thread pool of EvtJServer
     * by passing a custom module worker for said request.
     */
    public synchronized void queue(Request request) {
        ExecutionPool pool = (ExecutionPool) workerPoolExecutor.getExecutable();
        ModuleRunnable runnable = new ModuleRunnable(module, request);
        pool.queue(runnable);
    }

    public void resume() {
        if (state.isStopped()) {
            logger.warn("Server is stopped, can't resume");
            return;
        }
        state.resumed();
        module.onResume();
        resumeExecutors();
    }

    public void start() {
        if (state.isActive()) {
            logger.warn("Server is already running");
            return;
        }
        state.initialised();
        try {
            channelInitialize(port);
            startExecutors();
            module.onStart();
            state.started();
            logger.info("Server started");
        } catch (IOException e) {
            logger.error("EvtJServer could not bind the port " + port, e);
            System.exit(1);
        }
    }

    private void startExecutors() {
        workerPoolExecutor = new ExecutionThread(state, executionPool);
        dispatcherExecutor = new ExecutionThread(state, requestDispatcher);
        workerPoolExecutor.start();
        dispatcherExecutor.start();
    }

    public void stop() {
        if (state.isStopped()) {
            logger.warn("Server is already stopped");
            return;
        }
        state.stopped();
        module.onStop();
        stopExecutors();
        try {
            serverChannel.close();
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Server stopped");
    }

    private void stopExecutors() {
        try {
            dispatcherExecutor.interrupt();
            workerPoolExecutor.interrupt();
            dispatcherExecutor.join();
            workerPoolExecutor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void pauseExecutors() {
        dispatcherExecutor.pause();
        workerPoolExecutor.pause();
    }

    private void resumeExecutors() {
        dispatcherExecutor.unpause();
        workerPoolExecutor.unpause();
    }

}
