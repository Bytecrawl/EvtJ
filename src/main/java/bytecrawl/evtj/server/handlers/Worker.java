package bytecrawl.evtj.server.handlers;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJBalancer;
import bytecrawl.evtj.utils.EvtJConfiguration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker implements Handler {

    private Queue<Runnable> runnableQueue = new LinkedList<Runnable>();
    private ExecutorService workerPool;
    private Runnable currentRunnable;
    private EvtJBalancer balancer;

    public Worker(EvtJServer server) {
        int size = EvtJConfiguration.getInt(EvtJConfiguration.CONFIG_WORKER_POOL);
        workerPool = Executors.newFixedThreadPool(size);
        this.balancer = new EvtJBalancer();
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onRun() {
        balancer.setCycle();
        if(runnableQueue.size()>0) balancer.setActive();
        while (runnableQueue.size() > 0) {
            currentRunnable = popTask();
            workerPool.execute(currentRunnable);
        }
        balancer.balance();
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
