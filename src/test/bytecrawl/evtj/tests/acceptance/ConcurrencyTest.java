package bytecrawl.evtj.tests.acceptance;

import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.tests.mocks.*;
import bytecrawl.evtj.tests.mocks.modules.ConcurrentModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConcurrencyTest {

    private EvtJServer server;
    private Module module;

    @Before
    public final void setUp() throws ConfigurationException {
        module = new ConcurrentModule();
        server = new EvtJServer(4000, module);
        server.start();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @Test
    public void testConcurrency() {
        int threads = 4;
        int requests = 1000;

        ThreadedRequestLauncher threadedLauncher = new ThreadedRequestLauncher(threads, requests, "Hello World\n");
        threadedLauncher.run();

        assertEquals(threads*requests, ((ConcurrentModule)module).getCounter());
    }

}
