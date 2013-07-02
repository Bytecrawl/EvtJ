package bytecrawl.evtj.server.handlers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bytecrawl.evtj.server.EvtJServer;

public class Worker implements Handler {

	private Queue<Runnable> queue = new LinkedList<Runnable>();
	private ExecutorService worker_pool;

	public Worker(EvtJServer server) {
		worker_pool = Executors.newFixedThreadPool(server.getWorkerPoolSize());
	}

	public void onPause() {

	}

	public void onResume() {

	}

	public void onRun() {
		Runnable r;
		while (queue.size() > 0) {
			r = pollTask();
			worker_pool.execute(r);
		}
	}

	public void onStop() {
		worker_pool.shutdown();
	}

	public synchronized Runnable pollTask() {
		return queue.poll();
	}

	public synchronized void pushTask(Runnable r) {
		queue.add(r);
	}

}
