package bytecrawl.evtj.server.handlers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bytecrawl.evtj.server.EvtJServer;

public class WorkerHandler implements Handler {

	private Queue<Runnable> queue = new LinkedList<Runnable>();
	private ExecutorService worker_pool;

	public WorkerHandler(EvtJServer server) {
		worker_pool = Executors.newFixedThreadPool(server.getWorkerPoolSize());
	}

	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onRun() {
		Runnable r;
		while (queue.size() > 0) {
			r = pollTask();
			worker_pool.execute(r);
		}
	}

	@Override
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
