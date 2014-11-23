package bytecrawl.evtj.tests.mocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestLauncher {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public RequestLauncher(int port) {
        try {
            socket = new Socket("127.0.0.1", port);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String str) {
        out.write(str);
        out.flush();
    }

    public String read() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
