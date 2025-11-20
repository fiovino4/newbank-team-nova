package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUI {

    private final ClientConnection connection;
    private final CommandParser parser = new CommandParser();
    private final BufferedReader consoleReader =
            new BufferedReader(new InputStreamReader(System.in));

    public ConsoleUI(ClientConnection connection) {
        this.connection = connection;
    }

    public void start() throws IOException {

        //  NewBankClientHandler handles login part, i did not want to override this just yet
        System.out.println(connection.receive());
        System.out.print("> ");
        String username = consoleReader.readLine();
        connection.send(username);

        System.out.println(connection.receive());
        System.out.print("> ");
        String password = consoleReader.readLine();
        connection.send(password);

        System.out.println(connection.receive());

        String loginResult = connection.receive();
        System.out.println(loginResult);

        if (!loginResult.startsWith("Log In Successful")) {
            System.out.println("Client closing because login failed.");
            return;
        }

        Thread serverListener = createServerListenerThread();
        serverListener.setDaemon(true);
        serverListener.start();

        // Use parser after, login
        System.out.println("Type HELP for commands.");

        while (true) {
            System.out.print("> ");
            String input = consoleReader.readLine();
            if (input == null) {
                break;
            }

            ParsedCommand cmd = parser.parse(input);
            if (!cmd.isValid()) {
                System.out.println(cmd.getErrorMessage());
                continue;
            }

            String protocol = toProtocolString(cmd);
            connection.send(protocol);
        }
    }

    private String toProtocolString(ParsedCommand cmd) {
        StringBuilder sb = new StringBuilder(cmd.getName());
        for (String arg : cmd.getArguments()) {
            sb.append(' ').append(arg);
        }
        return sb.toString();
    }

    private Thread createServerListenerThread() {
        return new Thread(() -> {
            try {
                while (true) {
                    String response = connection.receive();
                    if (response == null) {
                        break;
                    }
                    System.out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server."); //Not sure but showing as disconnected
            }
        });
    }
}
