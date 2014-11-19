package bytecrawl.evtj.executors;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.server.EvtJServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionPool implements Executable {

    private Logger logger = LoggerFactory.getLogger("EvtJServer");

    private BlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<Runnable>(1000);
    private ExecutorService workerPool;
    private Runnable currentRunnable;

    public ExecutionPool(EvtJServer server) {
        int size = Configuration.getInt(Configuration.CFG_WORKER_POOL);
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
            //logger.debug("ExecutionPool pool interrupted.");
        }
    }

    public void onStop() {
        workerPool.shutdown();
    }

    public void pushTask(Runnable r) {
        runnableQueue.add(r);
    }

}
