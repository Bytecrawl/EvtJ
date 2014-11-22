package bytecrawl.evtj.executors;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.modules.ModuleRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionPool implements Executable {

    private Logger logger = LoggerFactory.getLogger("EvtJServer");

    private BlockingQueue<ModuleRunnable> runnableQueue = new ArrayBlockingQueue<ModuleRunnable>(1000);
    private ExecutorService workerPool;

    public ExecutionPool() {
        int size = Configuration.getInt(Configuration.CFG_WORKER_POOL);
        workerPool = Executors.newFixedThreadPool(size);
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onRun() {
        try {
            ModuleRunnable runnable = runnableQueue.take();
            workerPool.execute(runnable);
        } catch (InterruptedException e) {

        }
    }

    public void onStop() {
        workerPool.shutdown();
    }

    public void pushTask(ModuleRunnable r) {
        runnableQueue.add(r);
    }

}
