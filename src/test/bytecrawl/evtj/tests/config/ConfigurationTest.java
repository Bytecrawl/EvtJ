package bytecrawl.evtj.tests.config;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.config.ConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConfigurationTest {

    public ConfigurationTest() {

    }

    @org.junit.Test
    public void testFileOpen() {
        try {
            Configuration.newConfiguration("src/evtj.xml");
        } catch (ConfigurationException e) {
            fail("Exception thrown");
        }
    }

    @org.junit.Test
    public void testFileParse() {
        try {
            Configuration.newConfiguration("src/evtj.xml");
            assertEquals("11", Configuration.get(Configuration.CFG_WORKER_POOL));
            assertEquals("1025", Configuration.get(Configuration.CFG_BUFFER_SIZE));
            assertEquals("\\r\\n", Configuration.get(Configuration.CFG_SPLIT_SEQUENCE));
        } catch (ConfigurationException e) {
            fail("Exception thrown");
        }
    }

    @org.junit.Test
    public void testDefaultConfiguration() {
        Configuration.newConfiguration();
        assertEquals("10", Configuration.get(Configuration.CFG_WORKER_POOL));
        assertEquals("1024", Configuration.get(Configuration.CFG_BUFFER_SIZE));
        assertEquals("\n", Configuration.get(Configuration.CFG_SPLIT_SEQUENCE));
    }

    @org.junit.Test
    public void testNotAllowedOptionIsFiltered() {
        assertEquals(null, Configuration.get("non-authorized"));
    }

}
