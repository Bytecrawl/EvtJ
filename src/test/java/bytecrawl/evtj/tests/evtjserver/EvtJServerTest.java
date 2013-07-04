package bytecrawl.evtj.tests.evtjserver;

import static org.junit.Assert.*;

import bytecrawl.evtj.tests.utils.MockModule;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bytecrawl.evtj.server.EvtJModule;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJClient;
 
public class EvtJServerTest {

	private static final Logger logger =
	    LoggerFactory.getLogger(EvtJServerTest.class);
	
	private EvtJModule mock_module;
    private EvtJServer server;
	private EvtJClient client;

	public EvtJServerTest() {
		mock_module = new MockModule();
		client = new EvtJClient();
        server = new EvtJServer(4444, mock_module);
	}
    @Before
    public final void setUp() { server = new EvtJServer(4444, mock_module); server.start(); }

    @After
    public final void tearDown() { server.stop(); server = null; }

    @org.junit.Test
	public void ServerInitialisation() {
		assertEquals(server.isActive(), true);
	}
	
	@org.junit.Test
	public void ServerPause() {
		server.pause();
		assertEquals(server.isPaused(), true);
	}
	
	@org.junit.Test
	public void ServerStop() {
        server.stop();
		assertEquals(server.isActive(), false);
	}
	
	@org.junit.Test
	public void ServerPauseAndResume() {
		server.pause();
		assertEquals(server.isPaused(), true);
		server.resume();
		assertEquals(server.isPaused(), false);
	}
	
	@org.junit.Test
	public void ServerGetModule() {
		assertNotNull(server.getModule());
		assertEquals(server.getModule() == mock_module, true);
	}
	
	@org.junit.Test
	public void ServerGetConnectedClients() {
		assertEquals(server.getConnectedClients(), 0);
	}

	@org.junit.Test
	public void ServerAcceptedConnection() {
		assertEquals(server.getConnectedClients(), 0);
		server.newAcceptedConnection(client);
		assertEquals(server.getConnectedClients(), 1);
	}

}
