package bytecrawl.evtj.tests.evtjmodule;

import bytecrawl.evtj.server.EvtJModule;
import bytecrawl.evtj.server.EvtJModuleWorker;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.EvtJClient;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class EvtJModuleTest {

    private static final Logger logger =
            LoggerFactory.getLogger(EvtJModuleTest.class);

    private EvtJClient client;
    private EvtJServer server;
    private CustomMockModule mock_module;

    public EvtJModuleTest() {
        mock_module = new CustomMockModule();
        client = new EvtJClient();
        server = new EvtJServer(4444, mock_module);
    }

    public CustomMockModule getMockModule(EvtJServer server) {
        return (CustomMockModule)server.getModule();
    }

    @Before
    public final void setUp() { server.start(); }

    @After
    public final void tearDown() { server.stop(); server = null; }

    @org.junit.Test
    public void moduleWorkflowIntegrityTest() {
        server.start();
        assertEquals(1, getMockModule(server).getStarts());
        server.start();
        assertEquals(1, getMockModule(server).getStarts());
        server.pause();
        assertEquals(1, getMockModule(server).getPauses());
        server.pause();
        assertEquals(1, getMockModule(server).getPauses());
        server.resume();
        assertEquals(1, getMockModule(server).getResumes());
        server.stop();
        assertEquals(1, getMockModule(server).getStops());
        server.stop();
        assertEquals(1, getMockModule(server).getStops());
        server.resume();
        assertEquals(1, getMockModule(server).getStops());
        server.stop();
        assertEquals(1, getMockModule(server).getStops());
        server.start();
        server.pause();
        server.resume();
        server.stop();
        assertEquals(2, getMockModule(server).getStops());
        assertEquals(2, getMockModule(server).getStarts());
        assertEquals(2, getMockModule(server).getResumes());
        assertEquals(2, getMockModule(server).getPauses());
    }

    @org.junit.Test
    public void serverStatsConsistency() {
        server.start();
        assertEquals(server.getState().getStarts(), getMockModule(server).getStarts());
        server.start();
        assertEquals(server.getState().getStarts(), getMockModule(server).getStarts());
        server.pause();
        assertEquals(server.getState().getPauses(), getMockModule(server).getPauses());
        server.pause();
        assertEquals(server.getState().getPauses(), getMockModule(server).getPauses());
        server.resume();
        assertEquals(server.getState().getResumes(), getMockModule(server).getResumes());
        server.stop();
        assertEquals(server.getState().getStops(), getMockModule(server).getStops());
        server.stop();
        assertEquals(server.getState().getStops(), getMockModule(server).getStops());
        server.resume();
        assertEquals(server.getState().getResumes(), getMockModule(server).getStops());
        server.stop();
        assertEquals(server.getState().getStops(), getMockModule(server).getStops());
        server.start();
        server.pause();
        server.resume();
        server.stop();
        assertEquals(server.getState().getStops(), getMockModule(server).getStops());
        assertEquals(server.getState().getStarts(), getMockModule(server).getStarts());
        assertEquals(server.getState().getResumes(), getMockModule(server).getResumes());
        assertEquals(server.getState().getPauses(), getMockModule(server).getPauses());

    }

    public class CustomMockModule implements EvtJModule {

        private int pauses, resumes, starts, stops;

        public CustomMockModule() {
            reset();
        }

        public void reset() {
            pauses = 0;
            resumes = 0;
            starts = 0;
            stops = 0;
        }

        public int getPauses() { return pauses; }
        public int getResumes() { return resumes; }
        public int getStarts() { return starts; }
        public int getStops() { return stops; }

        public EvtJModuleWorker getWorker() {
            return null;
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
