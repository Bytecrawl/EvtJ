package bytecrawl.tests.server.modules;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.State;
import bytecrawl.tests.mocks.modules.CountModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class ModuleTest {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleTest.class);

    private CountModule countModule;
    private EvtJServer server;
    private State state;

    @Before
    public final void setUp() {
        countModule = new CountModule();
        server = new EvtJServer(4000, countModule);
        server.start();
        state = server.getState();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @Test
    public void testIntegrityInModuleState() {
        server.start();
        server.start();
        assertEquals(1, countModule.getStarts());

        server.pause();
        server.pause();
        assertEquals(1, countModule.getPauses());

        server.resume();
        assertEquals(1, countModule.getResumes());

        server.stop();
        server.stop();
        assertEquals(1, countModule.getStops());

        server.resume();
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

    @Test
    public void serverStatsAreConsistentWithModuleStats() {
        server.start();
        server.start();
        assertEquals(state.getStarts(), countModule.getStarts());

        server.pause();
        server.pause();
        assertEquals(state.getPauses(), countModule.getPauses());

        server.resume();
        assertEquals(state.getResumes(), countModule.getResumes());

        server.stop();
        server.stop();
        assertEquals(state.getStops(), countModule.getStops());

        server.resume();
        assertEquals(state.getResumes(), countModule.getStops());

        server.stop();
        assertEquals(state.getStops(), countModule.getStops());

        server.start();
        server.pause();
        server.resume();
        server.stop();
        assertEquals(state.getStops(), countModule.getStops());
        assertEquals(state.getStarts(), countModule.getStarts());
        assertEquals(state.getResumes(), countModule.getResumes());
        assertEquals(state.getPauses(), countModule.getPauses());

    }

}
