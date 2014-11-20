package bytecrawl.evtj.tests.server;

import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.State;
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
    private State state;
    private Client client;

    public EvtJServerTest() {
        mockModule = new MockModule();
        client = new Client();
    }

    @Before
    public final void setUp() throws ConfigurationException {
        server = new EvtJServer(4444, mockModule, "src/evtj.xml");
        server.start();
        state = server.getState();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @org.junit.Test
    public void ServerInitialisation() {
        assertEquals(state.isActive(), true);
    }

    @org.junit.Test
    public void ServerPause() {
        server.pause();
        assertEquals(state.isPaused(), true);
    }

    @org.junit.Test
    public void ServerStop() {
        server.stop();
        assertEquals(state.isActive(), false);
    }

    @org.junit.Test
    public void ServerPauseAndResume() {
        server.pause();
        assertEquals(state.isPaused(), true);
        server.resume();
        assertEquals(state.isPaused(), false);
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
