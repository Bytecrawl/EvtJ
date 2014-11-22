package bytecrawl.evtj.tests.acceptance;

import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.tests.mocks.EchoModule;
import bytecrawl.evtj.tests.mocks.MockClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EchoServerTest {

    private EvtJServer server;
    private EchoModule module;
    private MockClient client;

    @Before
    public final void setUp() throws ConfigurationException {
        module = new EchoModule();
        server = new EvtJServer(4000, module);
        server.start();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @Test
    public void testEchoIsCorrect() {
        client = new MockClient(4000);
        String message = "Hello world !";
        client.send(message);
        assertEquals(message, client.read());
    }

}
