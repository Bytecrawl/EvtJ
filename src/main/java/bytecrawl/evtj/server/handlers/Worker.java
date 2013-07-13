package bytecrawl.evtj.server.handlers;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class Worker implements Handler {

    private Logger logger = LoggerFactory.getLogger("EvtJServer");

    private BlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<Runnable>(1000);
    private ExecutorService workerPool;
    private Runnable currentRunnable;

    public Worker(EvtJServer server) {
        int size = EvtJConfiguration.getInt(EvtJConfiguration.CONFIG_WORKER_POOL);
        workerPool = Executors.newFixedThreadPool(size);
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onRun() {
        try {
            currentRunnable = runnableQueue.take();
            workerPool.execute(currentRunnable);
        } catch (InterruptedException e) {
            //logger.debug("Worker pool interrupted.");
        }
    }

    public void onStop() {
        workerPool.shutdown();
    }

    public void pushTask(Runnable r) {
        runnableQueue.add(r);
    }

}
