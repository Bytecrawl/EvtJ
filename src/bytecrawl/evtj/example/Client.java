package bytecrawl.evtj.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import bytecrawl.evtj.modules.chat.Message;
import bytecrawl.evtj.modules.chat.Register;
import bytecrawl.evtj.modules.chat.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Client implements Runnable {

	private Thread client_thread;
	
	private Socket sck;
	private BufferedWriter bw;
	private BufferedReader br;
	private String name;
	private String dest;
	private Gson gson = new GsonBuilder().create();
	
	public Client(String name, String dest, String addr, int port)
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
			this.dest = dest;
			client_thread = new Thread(this);
			client_thread.start();
		}
	}
	
	public Client(Socket sck)
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
			//System.out.println("[ "+name+" ] Sent: 	---\n"+content);
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
			//System.out.println("[ "+name+" ] Received:	---\n"+input);
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
		
		
		/*
		Pulse pulse = new Pulse();
		pulse.setType(Response.BEAT_TYPE);
		
		send(gson.toJson(pulse));
		
		read();
		*/
				
		Register register = new Register();
		register.setType(Response.REGISTER_TYPE);
		register.setName(name);
		
		send(gson.toJson(register));
		
		for(int i=0; i<1000; i++)
		{
			Message msg = new Message(name, dest, " Ola k ase ! ");
			msg.setType(Response.MESSAGE_TYPE);

			send(gson.toJson(msg));
			
			read();
		}
	
		close();
			
	}
}
