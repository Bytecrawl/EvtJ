package bytecrawl.evtj.server;

public interface EvtJModule {

    public EvtJModuleWorker getWorker();

    public void onPause();

    public void onResume();

    public void onStart();

    public void onStop();

}
