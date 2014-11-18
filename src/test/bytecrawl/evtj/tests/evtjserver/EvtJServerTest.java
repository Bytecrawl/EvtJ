package bytecrawl.evtj.tests.evtjserver;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Client;
import bytecrawl.evtj.tests.mocks.MockModule;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class EvtJServerTest {

    private static final Logger logger =
            LoggerFactory.getLogger(EvtJServerTest.class);

    private Module mockModule;
    private EvtJServer server;
    private Client client;

    public EvtJServerTest() {
        mockModule = new MockModule();
        client = new Client();
    }

    @Before
    public final void setUp() {
        server = new EvtJServer(4444, mockModule, "evtj.xml");
        server.start();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

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
        assertEquals(server.getModule() == mockModule, true);
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
