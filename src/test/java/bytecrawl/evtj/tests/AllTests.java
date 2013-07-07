package bytecrawl.evtj.tests;

import bytecrawl.evtj.tests.evtjconfiguration.EvtJConfigurationTest;
import bytecrawl.evtj.tests.evtjmodule.EvtJModuleTest;
import bytecrawl.evtj.tests.evtjserver.EvtJServerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EvtJServerTest.class, EvtJModuleTest.class, EvtJConfigurationTest.class})
public class AllTests {

} 