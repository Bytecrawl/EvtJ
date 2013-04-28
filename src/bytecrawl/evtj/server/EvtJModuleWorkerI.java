package bytecrawl.evtj.server;

import bytecrawl.evtj.utils.EvtJClient;

public interface EvtJModuleWorkerI extends Runnable {

	public void set(EvtJClient client, String command);

}
