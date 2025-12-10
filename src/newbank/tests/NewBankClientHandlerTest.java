package newbank.tests;

import newbank.server.NewBankClientHandler;
import org.junit.Test;

import static org.junit.Assert.*;

import java.net.ServerSocket;
import java.net.Socket;


public class NewBankClientHandlerTest {

    @Test
    public void clientDisconnects_intentional_disconnects_handlerTerminates() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            // connect client
            Socket clientSocket = new Socket("localhost", port);

            // accept on server side and create handler
            Socket serverSideSocket = serverSocket.accept();
            NewBankClientHandler handler = new NewBankClientHandler(serverSideSocket);
            handler.start();

            // simulate intentional client disconnect
            clientSocket.close();

            // wait for handler to finish (timeout to avoid hanging test)
            handler.join(2000);

            // handler should have terminated after detecting null input
            assertFalse("Handler should terminate when client disconnects", handler.isAlive());
        }
    }
}