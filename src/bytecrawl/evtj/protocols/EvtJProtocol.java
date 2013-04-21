package bytecrawl.evtj.protocols;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bytecrawl.evtj.protocols.evtj.PulseBack;
import bytecrawl.evtj.protocols.evtj.Response;

public class EvtJProtocol implements Runnable {

	private SocketChannel channel;

	private Response received, response;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private CharsetEncoder encoder;
	
	public EvtJProtocol(SocketChannel channel, String cmd) {
		this.channel = channel;
		received = gson.fromJson(cmd, Response.class);
	}
	
	public void run()
	{
		try
		{
			encoder = Charset.forName("UTF-8").newEncoder();
			int type = received.getType();
			
			response = new Response(
					channel.getLocalAddress().toString(),
					channel.getRemoteAddress().toString());
			
			switch(type)
			{
			case Response.BEAT_TYPE:
				response.setType(Response.RBEAT_TYPE);
				response.addPayload( new PulseBack());
				break;
				
			case Response.RBEAT_TYPE:
				break;
				
			}
			
			channel.write(encoder.encode(
					CharBuffer.wrap(gson.toJson(response))
					));
			
		}catch(IOException e) {
			try 
			{
				channel.close();
			}catch(IOException e2) {
				e.printStackTrace();
			}
		}
	}
}
