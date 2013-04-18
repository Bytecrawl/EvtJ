package bytecrawl.evtj.server.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import bytecrawl.evtj.server.EvtJServer;

public class ProtocolHandler {

	public static void run(EvtJServer server, SocketChannel ch) throws IOException
	{
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			String line = new String(buffer.array(), buffer.position(), buffer.remaining());
            //System.out.println("Received: "+line);
			
			if(line.equals("GET /users/online HTTP/1.1"))
			{
				//NetEvent evt = new NetEvent(sck, line);
				//server.addEventToBook(evt);
				/*response = server.getConnectedClients()+"\n";

					bw.write("HTTP/1.1 200 OK\n");
					bw.write("Content-Type: text/html\n");
					bw.write("Content-Length: "+response.length()+"\n");
					bw.write("\n");
					bw.write(response);
					bw.flush();
					bw.write("HTTP/1.1 200 OK\n");
					bw.write("Content-Type: text/html\n");
					bw.write("Content-Length: "+response.length()+"\n");
					bw.write("\n");
					bw.write(response);
					bw.flush();*/
					
			}else{
				
				buffer.put("Error dude \n".getBytes());
				ch.write(buffer);
			
			}
		
	}
}
