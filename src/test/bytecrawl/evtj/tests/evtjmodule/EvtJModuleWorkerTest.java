package bytecrawl.evtj.tests.evtjmodule;


import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.tests.mocks.MockModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EvtJModuleWorkerTest {

    private EvtJServer server;
    private MockModule mockModule = new MockModule();

    @Before
    public final void setUp() {
        server = new EvtJServer(4444, mockModule, "evtj.xml");
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
