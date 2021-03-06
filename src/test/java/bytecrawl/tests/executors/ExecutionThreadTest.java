package bytecrawl.tests.executors;


import bytecrawl.evtj.config.ConfigurationException;
import bytecrawl.evtj.executors.ExecutionThread;
import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.tests.mocks.MockExecutable;
import bytecrawl.tests.mocks.MockModule;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class ExecutionThreadTest {

    private MockModule module;
    private MockExecutable executable;
    private EvtJServer server;

    @Before
    public final void setUp() throws ConfigurationException {
        module = new MockModule();
        executable = new MockExecutable();
        server = new EvtJServer(4000, module);
        server.start();
    }

    @After
    public final void tearDown() {
        server.stop();
    }

    @org.junit.Test
    public void stoppingServerStopsExecutionThreads() {
        ExecutionThread thread = new ExecutionThread(server.getState(), executable);
        assertEquals(Thread.State.NEW, thread.getState());
        thread.start();
        assertEquals(Thread.State.RUNNABLE, thread.getState());
        server.stop();
        assertEquals(Thread.State.TERMINATED, thread.getState());
    }

    @org.junit.Test
    public void pausingServerDoesNotStopExecutionThreads() {
        ExecutionThread thread = new ExecutionThread(server.getState(), executable);
        assertEquals(Thread.State.NEW, thread.getState());
        thread.start();
        assertEquals(Thread.State.RUNNABLE, thread.getState());
        server.pause();
        assertEquals(Thread.State.RUNNABLE, thread.getState());
    }

}
