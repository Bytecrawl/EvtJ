package bytecrawl.evtj.server.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import bytecrawl.evtj.server.EvtJServer;

public class ProtocolHandler {

	public static void run(EvtJServer server, BufferedReader br, BufferedWriter bw) throws IOException
	{
			String line = br.readLine();
			String response;
			
			if(line.equals("GET /users/online HTTP/1.1"))
			{
				//NetEvent evt = new NetEvent(sck, line);
				//server.addEventToBook(evt);
				response = server.getConnectedClients()+"\n";

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
					bw.flush();
					
			}else{
				
				bw.write("Error dude \n");
				bw.flush();
			
			}
		
	}
}
