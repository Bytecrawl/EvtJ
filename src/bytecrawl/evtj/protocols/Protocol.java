package bytecrawl.evtj.protocols;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class Protocol implements Runnable {

	private SocketChannel channel;
	private String cmd = "";
	private String response;
	
	public Protocol(SocketChannel channel, String cmd) {
		this.channel = channel;
		this.cmd = cmd.substring(0, cmd.length()-1);
	}
	
	public void run()
	{
		try {
			CharsetEncoder enc = Charset.forName("UTF-8").newEncoder();  

			if (cmd.startsWith("fast")) {
				response = "fast\n";
				channel.write(enc.encode(CharBuffer.wrap(response)));
			}else if(cmd.startsWith("slow")) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					response = "slow\n";
					channel.write(enc.encode(CharBuffer.wrap(response)));
				}
				
			}
		}catch(IOException e) {
			try {
				channel.close();
			}catch(IOException e2) {
				e.printStackTrace();
			}
		}
	}
}
