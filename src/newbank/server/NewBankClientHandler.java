package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A client handler for the NewBank server that adds a confirmation
 * workflow around the NEWACCOUNT command. When a user issues
 * `NEWACCOUNT <accountName>`, the handler prompts for confirmation
 * before creating the account. All other commands are delegated to
 * the CommandProcessor.
 */
public class NewBankClientHandler extends Thread {

    private final NewBank bank;
    private final CommandProcessor commandProcessor;
    private final BufferedReader in;
    private final PrintWriter out;

    // State for pending confirmation
    private boolean awaitingConfirmation;
    private String pendingAccountName;

    public NewBankClientHandler(Socket s) throws IOException {
        this.bank = NewBank.getBank();
        this.commandProcessor = new CommandProcessor(bank);
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new PrintWriter(s.getOutputStream(), true);
        this.awaitingConfirmation = false;
        this.pendingAccountName = null;
    }

    @Override
    public void run() {
        try {
            CustomerID customer = null;

            // ===== LOGIN LOOP =====
            while (customer == null) {
                out.println("Enter Username");
                String userName = in.readLine();
                if (userName == null) {
                    return; // disconnected during login
                }

                out.println("Enter Password");
                String password = in.readLine();
                if (password == null) {
                    return; // disconnected during login
                }

                out.println("Checking Details...");
                Customer customerObj = bank.getCustomer(userName);
                if (customerObj == null) {
                    out.println("Log In Failed");
                    out.println("Username does not exist. Please try again.");
                    continue;
                }

                customer = bank.checkLogInDetails(userName, password);
                if (customer == null) {
                    out.println("Log In Failed");
                    out.println("Incorrect password. Please try again.");
                }
            }

            // ===== LOGGED-IN SESSION =====
            out.println("Log In Successful. Welcome " + customer.getKey() + "! What do you want to do?");
            while (true) {
                String request = in.readLine();
                if (request == null) {
                    break; // client disconnected
                }

                // handle confirmation state first
                if (awaitingConfirmation) {
                    handleConfirmation(request, customer);
                    continue;
                }

                // intercept NEWACCOUNT command
                String trimmed = request.trim();
                String[] parts = trimmed.split("\\s+", 2);
                if (parts.length > 0 && parts[0].equalsIgnoreCase("NEWACCOUNT")) {
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        out.println("Usage: NEWACCOUNT <accountName>");
                        continue;
                    }
                    pendingAccountName = parts[1].trim();
                    awaitingConfirmation = true;
                    out.println("Confirm a new account with the name '" + pendingAccountName
                            + "' by typing YES (or type EXIT to cancel).");
                    continue;
                }

                // delegate other commands to commandProcessor
                String response = commandProcessor.process(customer, request);
                out.println(response);
                if (response != null && response.startsWith("Session terminated")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error in client handler: " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                System.err.println("Error closing client handler streams: " + e.getMessage());
                e.printStackTrace(System.err);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void handleConfirmation(String response, CustomerID customer) {
        if (response.equalsIgnoreCase("YES")) {
            // create new account with zero balance
            Customer c = bank.getCustomer(customer.getKey());
            if (c != null) {
                c.addAccount(new Account(pendingAccountName, 0.0));
                out.println("Account '" + pendingAccountName + "' created successfully.");
            } else {
                out.println("FAIL: Customer not found.");
            }
            awaitingConfirmation = false;
            pendingAccountName = null;
        } else if (response.equalsIgnoreCase("EXIT")) {
            out.println("Account creation cancelled.");
            awaitingConfirmation = false;
            pendingAccountName = null;
        } else {
            out.println("Invalid response. Please type YES or EXIT.");
            out.println("Confirm a new account with the name '" + pendingAccountName
                    + "' by typing YES (or type EXIT to cancel).");
        }
    }
}