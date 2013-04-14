package bytecrawl.evtj.server.handlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Stack;

import bytecrawl.evtj.server.EvtJServer;
import bytecrawl.evtj.utils.SocketBook;

public class ExpeditorHandler implements Handler {

	private EvtJServer server;
	private SocketBook book;
	private Stack<Integer> disconnections = new Stack<Integer>();
	
	public ExpeditorHandler(EvtJServer server)
	{
		this.server = server;
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRun() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			Thread.currentThread().interrupt();
		}
		book = server.getSocketBook();
		
		for(int i=0; i<book.size(); i++)
		{
			try {
				Socket s = book.get(i);
				BufferedWriter bw;
				bw = new BufferedWriter(
						new OutputStreamWriter(s.getOutputStream())
					);

				bw.write("Heart beat\n");
				bw.flush();
			} catch (IOException e) {
				disconnections.push(i);
			}
		}

		int i;
		while(disconnections.size() > 0) {
			i = disconnections.pop();
			server.removeSocketFromBook(i);
			System.out.println("Client disconnected in index "+i);
		}
	}

}
