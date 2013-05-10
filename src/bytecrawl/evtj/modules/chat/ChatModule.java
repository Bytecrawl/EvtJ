package bytecrawl.evtj.modules.chat;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import bytecrawl.evtj.server.EvtJModule;
import bytecrawl.evtj.utils.EvtJClient;

public class ChatModule extends EvtJModule {

	private Map<String, EvtJClient> user_list = new HashMap<String, EvtJClient>();
	private Logger logger = Logger.getLogger("app");
	
	public synchronized void registerClient(EvtJClient client, String name)
	{
		user_list.put(name, client);
	}

	public synchronized EvtJClient getClient(String name) { return user_list.get(name); }

	public ChatWorker getWorker()
	{
		return new ChatWorker(this);
	}
	
	public synchronized boolean client_exists(String name)
	{
		return user_list.containsKey(name);
	}
	
}
