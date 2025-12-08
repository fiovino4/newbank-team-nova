package newbank.client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 14002; // change if your server uses a different port

    private final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }

        new ExampleClient().start(host, port);
    }

    public void start(String host, int port) {
        Socket socket = null;
        BufferedReader serverIn = null;
        PrintWriter serverOut = null;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        Thread readerThread = null;

        try {
            socket = new Socket(host, port);
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // ----------------------------
            // Reader thread (LM-105 core)
            // ----------------------------
            final Socket s = socket;
            final BufferedReader in = serverIn;
            final PrintWriter out = serverOut;

            readerThread = new Thread(() -> {
                try {
                    String line;
                    while (running.get() && (line = in.readLine()) != null) {
                        System.out.println(line);
                    }

                    // readLine() returning null = server closed connection
                    if (running.get()) {
                        System.out.println("Connection closed by server.");
                    }

                } catch (IOException e) {
                    if (running.get()) {
                        System.out.println("Connection closed by server.");
                    }
                } finally {
                    running.set(false);
                    closeQuietly(in);
                    // PrintWriter doesn't implement AutoCloseable cleanly in all setups,
                    // but calling close is safe.
                    try { out.close(); } catch (Exception ignored) {}
                    closeQuietly(s);
                }
            }, "server-reader");

            readerThread.start();

            // ----------------------------
            // Main input loop
            // ----------------------------
            while (running.get()) {
                String userInput = console.readLine();

                // End of stdin
                if (userInput == null) break;

                // If server already disconnected, exit cleanly
                if (!running.get()) break;

                try {
                    serverOut.println(userInput);
                    // println with autoFlush true is enough
                } catch (Exception e) {
                    if (running.get()) {
                        System.out.println("Connection closed by server.");
                    }
                    break;
                }
            }

        } catch (IOException e) {
            // Covers disconnect on connect / immediate failure
            System.out.println("Connection closed by server.");
        } finally {
            running.set(false);
            closeQuietly(serverIn);
            if (serverOut != null) {
                try { serverOut.close(); } catch (Exception ignored) {}
            }
            closeQuietly(socket);

            if (readerThread != null) {
                try {
                    readerThread.join(1000);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private static void closeQuietly(AutoCloseable c) {
        try {
            if (c != null) c.close();
        } catch (Exception ignored) {}
    }
}