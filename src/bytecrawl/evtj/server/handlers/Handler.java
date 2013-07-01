package bytecrawl.evtj.server.handlers;

public interface Handler {

	public void onPause();
	
	public void onResume();
	
	public void onStop();
	
	public void onRun();

}
