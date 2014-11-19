package bytecrawl.evtj.tests.server.modules;


import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.tests.mocks.MockModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ModuleWorkerTest {

    private EvtJServer server;
    private MockModule mockModule = new MockModule();

    @Before
    public final void setUp() throws ConfigurationException {
        server = new EvtJServer(4444, mockModule, "src/evtj.xml");
        server.start();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @Test
    public void testBuffer() {

    }
}
