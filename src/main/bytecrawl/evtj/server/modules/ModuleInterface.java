package bytecrawl.evtj.server.modules;

public interface ModuleInterface {

    public Module getWorker() throws IllegalAccessException, InstantiationException;

    public void onPause();

    public void onResume();

    public void onStart();

    public void onStop();

}