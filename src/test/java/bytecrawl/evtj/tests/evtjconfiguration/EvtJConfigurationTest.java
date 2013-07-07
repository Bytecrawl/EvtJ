package bytecrawl.evtj.tests.evtjconfiguration;

import bytecrawl.evtj.utils.EvtJConfiguration;

import static org.junit.Assert.assertEquals;

public class EvtJConfigurationTest {

    public EvtJConfigurationTest() {

    }

    @org.junit.Test
    public void testFileOpen() {
        EvtJConfiguration.newConfiguration("evtj.xml");
        assertEquals("10", EvtJConfiguration.get("worker-pool"));
        assertEquals("\n", EvtJConfiguration.get("split-sequence"));
        assertEquals("1024", EvtJConfiguration.get("buffer-size"));
    }

}
