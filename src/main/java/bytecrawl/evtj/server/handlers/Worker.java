package bytecrawl.evtj.server.handlers;

import bytecrawl.evtj.server.EvtJServer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker implements Handler {

    private Queue<Runnable> runnableQueue = new LinkedList<Runnable>();
    private ExecutorService workerPool;
    private Runnable currentRunnable;

    public Worker(EvtJServer server) {
        workerPool = Executors.newFixedThreadPool(server.getWorkerPoolSize());
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onRun() {
        while (runnableQueue.size() > 0) {
            currentRunnable = popTask();
            workerPool.execute(currentRunnable);
        }
    }

    public void onStop() {
        workerPool.shutdown();
    }

    private Runnable popTask() {
        return runnableQueue.poll();
    }

    public synchronized void pushTask(Runnable r) {
        runnableQueue.add(r);
    }

}
