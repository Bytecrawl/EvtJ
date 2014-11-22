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
        client.send(message+"\n");
        assertEquals(message, client.read());
    }

    @Test
    public void testEchoIsCorrectOnMultipleWrites() {
        client = new MockClient(4000);
        String m1 = "Hello ";
        String m2 = "World ";
        String m3 = "!";
        String m4 = "\n";
        client.send(m1);
        client.send(m2);
        client.send(m3);
        client.send(m4);
        assertEquals(m1 + m2 + m3, client.read());
    }

    @Test
    public void testEchoIsCorrectOnHalfRequest() {
        client = new MockClient(4000);
        client.send("Complete request\nHalf ");
        assertEquals("Complete request", client.read());
        client.send("request\n");
        assertEquals("Half request", client.read());
    }

}
