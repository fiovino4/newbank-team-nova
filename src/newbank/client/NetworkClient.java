package newbank.client;

import java.io.IOException;

/**
 * Abstraction for network communication between client UI and server.
 * Allows mocking and testing without requiring an actual socket.
 */
public interface NetworkClient {

    void send (String message) throws IOException;
    String receive() throws IOException;
}
