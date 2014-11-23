package bytecrawl.evtj.tests.mocks;


import java.util.ArrayList;
import java.util.List;

public class ThreadedRequestLauncher {

    private int numRequests;
    private int numThreads;
    private String request;

    class RequestRunnable implements Runnable {
        private int numRequests;
        private String request;
        private RequestLauncher launcher;

        public RequestRunnable(int numRequests, String request) {
            launcher = new RequestLauncher(4000);
            this.numRequests = numRequests;
            this.request = request;
        }

        @Override
        public void run() {
            for ( int i=0; i<numRequests; i++) {
                launcher.send(request);
                launcher.read();
            }
        }
    }

    public ThreadedRequestLauncher(int numThreads, int numRequests, String request) {
        this.numRequests = numRequests;
        this.numThreads = numThreads;
        this.request = request;
    }

    public void run() {
        List<Thread> threadList = new ArrayList<Thread>();
        for ( int i=0; i<numThreads; i++) {
            Thread t = new Thread(new RequestRunnable(numRequests, request));
            t.start();
            threadList.add(t);
        }
        try {
            for (Thread t : threadList) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
