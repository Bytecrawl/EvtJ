package bytecrawl.evtj.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import bytecrawl.evtj.protocols.evtj.Pulse;
import bytecrawl.evtj.protocols.evtj.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EvtJClient implements Runnable {

	private Thread client_thread;
	
	private Socket sck;
	private BufferedWriter bw;
	private BufferedReader br;
	private String name;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public EvtJClient(String name, String addr, int port)
	{
		try
		{
			sck = new Socket(addr, port);
			bw = new BufferedWriter( 
					new OutputStreamWriter(sck.getOutputStream())
					);
			br = new BufferedReader(
					new InputStreamReader(sck.getInputStream())
					);
		}catch(UnknownHostException e){
			System.out.println(e.getMessage());
		}catch(IOException e){
			System.out.println(e.getMessage());
		}finally{
			this.name = name;
			client_thread = new Thread(this);
			client_thread.start();
		}
	}
	
	public EvtJClient(Socket sck)
	{
		try
		{
			this.sck = sck;
			bw = new BufferedWriter( 
					new OutputStreamWriter(sck.getOutputStream())
					);
			br = new BufferedReader(
					new InputStreamReader(sck.getInputStream())
					);
		}catch(UnknownHostException e){
			System.out.println(e.getMessage());
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public synchronized void start()
	{
		this.notifyAll();
	}
	
	private boolean send(String content)
	{
		try
		{
			bw.write(content+"\n");
			bw.flush();
			System.out.println("[ "+name+" ] Sent: 	---\n"+content);
			return true;
		}catch(IOException e){
			System.out.println(e.getMessage());
			return false;
		}

	}
	
	private String read()
	{
		String input = "";
		try
		{
			char[] buffer = new char[1024];
			br.read(buffer, 0, 1024);
			input = new String(buffer);
			System.out.println("[ "+name+" ] Received:	---\n"+input);
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
				
		return input;
	}

	public void close()
	{
		try
		{
			bw.flush();
			bw.close();
			br.close();
			sck.close();
			client_thread.interrupt();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void run() {
		try
		{
			this.wait();
		}catch(InterruptedException e) {
				
		}
		
		Response message = new Response(
				sck.getLocalAddress().toString(),
				sck.getRemoteSocketAddress().toString(),
				Response.BEAT_TYPE);
		
		message.addPayload(new Pulse());
		
		send(gson.toJson(message));
		
		read();
		
		close();
			
	}
}
