package newbank.client;

import java.io.*;

public class ClientConnection implements NetworkClient{

    private final BufferedReader serverIn;
    private final PrintWriter serverOut;

    public ClientConnection(String host, int port) throws IOException {
        var socket = new java.net.Socket(host, port);
        this.serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.serverOut = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void send(String message) {
        serverOut.println(message);
    }

    @Override
    public String receive() throws IOException {
        return serverIn.readLine();
    }
}
