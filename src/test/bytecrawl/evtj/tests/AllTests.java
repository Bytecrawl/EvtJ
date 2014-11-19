package bytecrawl.evtj.tests;

import bytecrawl.evtj.tests.config.ConfigurationTest;
import bytecrawl.evtj.tests.server.EvtJServerTest;
import bytecrawl.evtj.tests.server.modules.ModuleTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({EvtJServerTest.class, ModuleTest.class, ConfigurationTest.class})
public class AllTests {

} 