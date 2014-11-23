package bytecrawl.tests.server;

import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.State;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Client;
import bytecrawl.tests.mocks.MockModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class StateTest {

    private static final Logger logger =
            LoggerFactory.getLogger(StateTest.class);

    private Module mockModule;
    private EvtJServer server;
    private State state;
    private Client client;

    public StateTest() {
        mockModule = new MockModule();
        client = new Client();
    }

    @Before
    public final void setUp() throws ConfigurationException, InterruptedException {
        server = new EvtJServer(4000, mockModule);
        server.start();
        state = server.getState();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @Test
    public void testServerInitialisation() {
        assertEquals(state.isActive(), true);
    }

    @Test
    public void testServerPause() {
        server.pause();
        assertEquals(state.isPaused(), true);
    }

    @Test
    public void testServerStop() {
        server.stop();
        assertEquals(state.isActive(), false);
    }

    @Test
    public void testServerPauseAndResume() {
        server.pause();
        assertEquals(state.isPaused(), true);
        server.resume();
        assertEquals(state.isPaused(), false);
    }

    @Test
    public void testConnections() {
        assertEquals(state.getConnections(), 0);
    }

    @Test
    public void testNewConnection() {
        assertEquals(state.getConnections(), 0);
        state.newConnection(client);
        assertEquals(state.getConnections(), 1);
    }

    @Test
    public void testServerDisconnection() {
        assertEquals(state.getConnections(), 0);
        state.newConnection(client);
        state.newDisconnection(client);
        assertEquals(state.getConnections(), 0);
    }

}
