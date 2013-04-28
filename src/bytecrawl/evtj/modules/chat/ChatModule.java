package bytecrawl.evtj.modules.chat;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import bytecrawl.evtj.server.EvtJModule;
import bytecrawl.evtj.server.EvtJModuleI;
import bytecrawl.evtj.utils.EvtJClient;

public class ChatModule extends EvtJModule implements EvtJModuleI {

	private Map<String, EvtJClient> user_list = new HashMap<String, EvtJClient>();
	private Logger logger = Logger.getLogger("app");
	
	public ChatWorker getWorker()
	{
		return new ChatWorker(this);
	}
	
	public synchronized void registerClient(EvtJClient client, String name)
	{
		user_list.put(name, client);
		logger.info("Registered client: "+name);
	}

	public synchronized EvtJClient getClient(String name) { return user_list.get(name); }
	
}
