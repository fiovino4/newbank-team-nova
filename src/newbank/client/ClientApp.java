package newbank.client;

public class ClientApp {

    public static void main(String[] args) {
        try {
            NetworkClient connection = new ClientConnection("localhost", 14002);
            ConsoleUI ui = new ConsoleUI(connection);
            ui.start();
        } catch (Exception e) {
            System.out.println("Failed to start client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
