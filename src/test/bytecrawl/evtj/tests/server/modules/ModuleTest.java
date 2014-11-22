package bytecrawl.evtj.tests.server.modules;

import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.server.modules.Module;
import bytecrawl.evtj.server.requests.Client;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class ModuleTest {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleTest.class);

    private EvtJServer server;
    private CounterModule counterModule;

    public ModuleTest() {
        counterModule = new CounterModule();
    }

    @Before
    public final void setUp() throws ConfigurationException {
        server = new EvtJServer(4000, counterModule, "src/evtj.xml");
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
        assertEquals(1, counterModule.getStarts());
        server.start();
        assertEquals(1, counterModule.getStarts());

        server.pause();
        assertEquals(1, counterModule.getPauses());
        server.pause();
        assertEquals(1, counterModule.getPauses());

        server.resume();
        assertEquals(1, counterModule.getResumes());

        server.stop();
        assertEquals(1, counterModule.getStops());
        server.stop();
        assertEquals(1, counterModule.getStops());

        server.resume();
        assertEquals(1, counterModule.getStops());

        server.stop();
        assertEquals(1, counterModule.getStops());

        server.start();
        server.pause();
        server.resume();
        server.stop();
        assertEquals(2, counterModule.getStops());
        assertEquals(2, counterModule.getStarts());
        assertEquals(2, counterModule.getResumes());
        assertEquals(2, counterModule.getPauses());
    }

    @org.junit.Test
    public void serverStatsConsistency() {
        server.start();
        assertEquals(server.getState().getStarts(), counterModule.getStarts());
        server.start();
        assertEquals(server.getState().getStarts(), counterModule.getStarts());

        server.pause();
        assertEquals(server.getState().getPauses(), counterModule.getPauses());
        server.pause();
        assertEquals(server.getState().getPauses(), counterModule.getPauses());

        server.resume();
        assertEquals(server.getState().getResumes(), counterModule.getResumes());

        server.stop();
        assertEquals(server.getState().getStops(), counterModule.getStops());
        server.stop();
        assertEquals(server.getState().getStops(), counterModule.getStops());

        server.resume();
        assertEquals(server.getState().getResumes(), counterModule.getStops());

        server.stop();
        assertEquals(server.getState().getStops(), counterModule.getStops());

        server.start();
        server.pause();
        server.resume();
        server.stop();
        assertEquals(server.getState().getStops(), counterModule.getStops());
        assertEquals(server.getState().getStarts(), counterModule.getStarts());
        assertEquals(server.getState().getResumes(), counterModule.getResumes());
        assertEquals(server.getState().getPauses(), counterModule.getPauses());

    }

    public class CounterModule extends Module {

        private int pauses, resumes, starts, stops;

        public CounterModule() {
            reset();
        }

        public void reset() {
            pauses = 0;
            resumes = 0;
            starts = 0;
            stops = 0;
        }

        public int getPauses() {
            return pauses;
        }

        public int getResumes() {
            return resumes;
        }

        public int getStarts() {
            return starts;
        }

        public int getStops() {
            return stops;
        }

        @Override
        public void serveRequest(Client client, String request) {

        }

        public void onPause() {
            pauses++;
        }

        public void onResume() {
            resumes++;
        }

        public void onStart() {
            starts++;
        }

        public void onStop() {
            stops++;
        }
    }

}
