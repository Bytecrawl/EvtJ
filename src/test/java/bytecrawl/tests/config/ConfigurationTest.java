package bytecrawl.tests.config;

import bytecrawl.evtj.config.Configuration;
import bytecrawl.evtj.config.ConfigurationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConfigurationTest {

    @Test
    public void testFileOpen() {
        try {
            Configuration.newConfiguration("src/test/resources/evtj.xml");
        } catch (ConfigurationException e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testFileParse() {
        try {
            Configuration.newConfiguration("src/test/resources/evtj.xml");
            assertEquals("11", Configuration.get(Configuration.CFG_WORKER_POOL));
            assertEquals("1025", Configuration.get(Configuration.CFG_BUFFER_SIZE));
            assertEquals("\\r\\n", Configuration.get(Configuration.CFG_SPLIT_SEQUENCE));
        } catch (ConfigurationException e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testDefaultConfiguration() {
        Configuration.newConfiguration();
        assertEquals("10", Configuration.get(Configuration.CFG_WORKER_POOL));
        assertEquals("1024", Configuration.get(Configuration.CFG_BUFFER_SIZE));
        assertEquals("\n", Configuration.get(Configuration.CFG_SPLIT_SEQUENCE));
    }

    @Test
    public void testNotAllowedOptionIsFiltered() {
        Configuration.newConfiguration();
        assertEquals(null, Configuration.get("non-authorized"));
    }

}
