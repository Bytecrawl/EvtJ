package bytecrawl.evtj.tests.server.modules;

import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.tests.mocks.modules.CountModule;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class ModuleTest {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleTest.class);

    private EvtJServer server;
    private CountModule countModule;

    public ModuleTest() {
        countModule = new CountModule();
    }

    @Before
    public final void setUp() throws ConfigurationException {
        server = new EvtJServer(4000, countModule, "src/evtj.xml");
        server.start();
    }

    @After
    public final void tearDown() {
        server.stop();
        server = null;
    }

    @org.junit.Test
    public void moduleWorkflowIntegrityTest() {

        server.start();
        assertEquals(1, countModule.getStarts());
        server.start();
        assertEquals(1, countModule.getStarts());

        server.pause();
        assertEquals(1, countModule.getPauses());
        server.pause();
        assertEquals(1, countModule.getPauses());

        server.resume();
        assertEquals(1, countModule.getResumes());

        server.stop();
        assertEquals(1, countModule.getStops());
        server.stop();
        assertEquals(1, countModule.getStops());

        server.resume();
        assertEquals(1, countModule.getStops());

        server.stop();
        assertEquals(1, countModule.getStops());

        server.start();
        server.pause();
        server.resume();
        server.stop();
        assertEquals(2, countModule.getStops());
        assertEquals(2, countModule.getStarts());
        assertEquals(2, countModule.getResumes());
        assertEquals(2, countModule.getPauses());
    }

    @org.junit.Test
    public void serverStatsConsistency() {
        server.start();
        assertEquals(server.getState().getStarts(), countModule.getStarts());
        server.start();
        assertEquals(server.getState().getStarts(), countModule.getStarts());

        server.pause();
        assertEquals(server.getState().getPauses(), countModule.getPauses());
        server.pause();
        assertEquals(server.getState().getPauses(), countModule.getPauses());

        server.resume();
        assertEquals(server.getState().getResumes(), countModule.getResumes());

        server.stop();
        assertEquals(server.getState().getStops(), countModule.getStops());
        server.stop();
        assertEquals(server.getState().getStops(), countModule.getStops());

        server.resume();
        assertEquals(server.getState().getResumes(), countModule.getStops());

        server.stop();
        assertEquals(server.getState().getStops(), countModule.getStops());

        server.start();
        server.pause();
        server.resume();
        server.stop();
        assertEquals(server.getState().getStops(), countModule.getStops());
        assertEquals(server.getState().getStarts(), countModule.getStarts());
        assertEquals(server.getState().getResumes(), countModule.getResumes());
        assertEquals(server.getState().getPauses(), countModule.getPauses());

    }

}
