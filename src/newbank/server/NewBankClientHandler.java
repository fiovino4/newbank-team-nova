package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{
	
	private final NewBank bank;
    private final CommandProcessor commandProcessor;
	private final BufferedReader in;
	private final PrintWriter out;
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
        commandProcessor = new CommandProcessor(bank);
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run() {
		try {
			CustomerID customer = null;

			// ===== LOGIN LOOP =====
			while (customer == null) {
				// ask for user name
				out.println("Enter Username");
				String userName = in.readLine();
				if (userName == null) {
					// client disconnected during login
					return;
				}

				// ask for password
				out.println("Enter Password");
				String password = in.readLine();
				if (password == null) {
					// client disconnected during login
					return;
				}

				out.println("Checking Details...");

				// authenticate user and get customer ID token from bank for use in subsequent requests
				// Check if username exists
				Customer customerObj = bank.getCustomer(userName);
				if (customerObj == null) {
					out.println("Log In Failed");
					out.println("Username does not exist. Please try again.");
					continue;  // retry
				}

				// Check password correctness
				customer = bank.checkLogInDetails(userName, password);
				if (customer == null) {
					out.println("Log In Failed");
					out.println("Incorrect password. Please try again.");
				}
			}

			// ===== LOGGED-IN SESSION =====
			//out.println("Log In Successful. What do you want to do?");
			out.println("Log In Successful. Welcome " + customer.getKey() + "! What do you want to do?");
			while (true) {
				String request = in.readLine();
				if (request == null) {
					// client disconnected after login
					break;
				}
				System.out.println("Request from " + customer.getKey());
				String response = commandProcessor.process(customer, request);
				out.println(response);

                if (response != null && response.startsWith("Session terminated")) {
                    break;  // LOGOUT/EXIT/QUIT
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
}
