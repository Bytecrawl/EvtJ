package bytecrawl.evtj.tests.evtjconfiguration;

import bytecrawl.evtj.config.Configuration;

import static org.junit.Assert.assertEquals;

public class EvtJConfigurationTest {

    public EvtJConfigurationTest() {

    }

    @org.junit.Test
    public void testFileOpen() {
        Configuration.newConfiguration("evtj.xml");
        assertEquals("10", Configuration.get("worker-pool"));
        assertEquals("\n", Configuration.get("split-sequence"));
        assertEquals("1024", Configuration.get("buffer-size"));
    }

}
