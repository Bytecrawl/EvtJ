package bytecrawl.evtj.server.modules;

public interface EvtJModule {

    public EvtJModuleWorker getWorker();

    public void onPause();

    public void onResume();

    public void onStart();

    public void onStop();

}
