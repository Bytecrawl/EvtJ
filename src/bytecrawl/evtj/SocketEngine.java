package bytecrawl.evtj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketEngine {
	
	private Socket sck;

	private BufferedWriter bw;
	private BufferedReader br;
	
	private String input;
	
	public SocketEngine(String addr, int port)
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
		}
	}
	
	public SocketEngine(Socket sck)
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
	
	public boolean send(String content)
	{
		if(isClosed())
			return false;
		if(!isBound())
			return false;
		if(!isConnected())
			return false;
		try
		{
			bw.write(content+"\n");
			bw.flush();
			return true;
		}catch(IOException e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public String recv()
	{
		if(isClosed())
			return "";
		if(!isBound())
			return "";
		if(!isConnected())
			return "";
		
		input = "";
		try
		{
			input = br.readLine();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		return input;
	}

	public void close()
	{
		try
		{
			if(sck!=null)
				sck.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public boolean isBound()
	{
		return sck.isBound();
	}
	
	public boolean isClosed()
	{
		return sck.isClosed();
	}

	public boolean isConnected()
	{
		return sck.isConnected();
	}
}
