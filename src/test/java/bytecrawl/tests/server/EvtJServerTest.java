package bytecrawl.tests.server;

import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.tests.mocks.MockModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EvtJServerTest {

    private EvtJServer server;
    private Module module;

    @Before
    public final void setUp() throws ConfigurationException, InterruptedException {
        module = new MockModule();
        server = new EvtJServer(4000, module);
        server.start();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @Test
    public void ServerGetModule() {
        assertNotNull(server.getModule());
        assertEquals(module, server.getModule());
    }
}
