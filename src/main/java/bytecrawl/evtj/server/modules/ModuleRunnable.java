package bytecrawl.evtj.server.modules;

import bytecrawl.evtj.server.requests.Request;

/**
 * This class knows how to execute a Module, implements runnable
 * and so, we can queue it in the ExecutionPool of ModuleRunnables
 * to serve requests.
 */
public class ModuleRunnable implements Runnable {

    private Module module;
    private Request request;

    public ModuleRunnable(Module module, Request request) {
        this.module = module;
        this.request = request;
    }

    @Override
    public void run() {
        module.serveRequest(request);
    }
}
