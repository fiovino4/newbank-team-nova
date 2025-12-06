package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUI {

    private final NetworkClient connection;
    private final CommandParser parser = new CommandParser();
    private final BufferedReader consoleReader =
            new BufferedReader(new InputStreamReader(System.in));

    public ConsoleUI(NetworkClient connection) {
        this.connection = connection;
    }

    public void start() throws IOException {

        // ===== LOGIN LOOP =====
        while (true) {
            // NewBankClientHandler handles login prompts and messages.
            System.out.println(connection.receive()); // "Enter Username ..."
            System.out.print("> ");
            String username = consoleReader.readLine();
            connection.send(username);

            System.out.println(connection.receive()); // "Enter Password ..."
            System.out.print("> ");
            String password = consoleReader.readLine();
            connection.send(password);

            System.out.println(connection.receive()); // "Checking Details..."

            String loginResult = connection.receive();
            System.out.println(loginResult);

            if (loginResult != null && loginResult.startsWith("Log In Successful")) {
                break;
            }

            String extra = connection.receive();
            if (extra != null && !extra.isBlank()) {
                System.out.println(extra);
            }
        }

        // ===== LOGGED-IN SESSION =====
        while (true) {
            System.out.println("Enter command (type HELP for a list of commands):");
            System.out.print("> ");
            String input = consoleReader.readLine();
            if (input == null) {
                break; // console closed
            }

            // Allow multi-step flows for certain commands when the user
            // types just the command name without parameters.
            String trimmed = input.trim();

            if (trimmed.equalsIgnoreCase("TRANSFER")) {
                input = buildInteractiveTransferCommand();
                if (input == null) { // user cancelled
                    continue;
                }
            } else if (trimmed.equalsIgnoreCase("OFFERLOAN")) {
                input = buildInteractiveOfferLoanCommand();
                if (input == null) {
                    continue;
                }
            } else if (trimmed.equalsIgnoreCase("REQUESTLOAN")) {
                input = buildInteractiveRequestLoanCommand();
                if (input == null) {
                    continue;
                }
            } else if (trimmed.equalsIgnoreCase("CREATEACCOUNT")) {
                input = buildInteractiveCreateAccountCommand();
                if (input == null) {
                    continue;
                }
            }

            ParsedCommand cmd = parser.parse(input);
            if (!cmd.isValid()) {
                System.out.println(cmd.getErrorMessage());
                continue;
            }

            String protocol = toProtocolString(cmd);
            connection.send(protocol);

            // Special handling for HELP which returns multiple lines.
            if ("HELP".equals(cmd.getName())) {
               boolean ok =  readMultilineResponseUntilEndMarker("END_OF_HELP");
               if(!ok){
                   break;
               }
                continue;
            }

            // Special handling for account listing (multi-line).
            if ("SHOWMYACCOUNTS".equals(cmd.getName())
                    || "BALANCE".equals(cmd.getName())
                    || "BALANCES".equals(cmd.getName())) {
                boolean ok = readMultilineResponseUntilEndMarker("END_OF_ACCOUNTS");
                if(!ok){
                    break;
                }
                continue;
            }

            if ("SHOWAVAILABLELOANS".equals(cmd.getName())) {
                boolean ok = readMultilineResponseUntilEndMarker("END_OF_LOANS");
                if (!ok) {
                    break;
                }
                continue;
            }

            // Special handling for user's own loans (multi-line)
            if ("MYLOANS".equals(cmd.getName())) {
                boolean ok = readMultilineResponseUntilEndMarker("END_OF_MYLOANS");
                if (!ok) {
                    break;
                }
                continue;
            }

            // Default: single-line response
            String response;
            try {
                response = connection.receive();
            } catch (IOException e) {
                System.out.println("Connection closed by server.");
                break;
            }

            if (response == null) {
                System.out.println("Connection closed by server.");
                break;
            }

            System.out.println(response);

            if (response.startsWith("Session terminated")) {
                break;
            }
        }
    }

    private String toProtocolString(ParsedCommand cmd) {
        StringBuilder sb = new StringBuilder(cmd.getName());
        for (String arg : cmd.getArguments()) {
            sb.append(' ').append(arg);
        }
        return sb.toString();
    }

    private String promptForOrExit(String label, String formatHint) throws IOException {
        System.out.println("Enter " + label + " (" + formatHint + ", or type EXIT to cancel):");
        System.out.print("> ");
        String value = consoleReader.readLine();
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if ("EXIT".equalsIgnoreCase(trimmed)) {
            return null;
        }
        return value;
    }


     //Helper to confirm an action with YES, or EXIT to cancel.
    // Returns true only if the user explicitly types YES or Y.
     private boolean confirmYesOrExit(String message) throws IOException {
        System.out.println(message);
        System.out.print("> ");
        String value = consoleReader.readLine();
        if (value == null) {
            return false;
        }
        String trimmed = value.trim().toLowerCase();
        if ("exit".equals(trimmed)) {
            System.out.println("Operation cancelled.");
            return false;
        }
        return "yes".equals(trimmed) || "y".equals(trimmed);
    }


    // Helper to print a consistent cancellation message for a command.
    private String cancelCommand(String commandName) {
        System.out.println(commandName.toUpperCase() + " process cancelled.");
        return null;
    }

    private String buildInteractiveTransferCommand() throws IOException {
        String from = promptForOrExit("source account name",
                "existing account, e.g. Main");
        if (from == null) {
            return cancelCommand("TRANSFER");
        }

        String to = promptForOrExit("destination account name",
                "existing account, e.g. Savings");
        if (to == null) {
            return cancelCommand("TRANSFER");
        }

        String amount = promptForOrExit("amount",
                "numeric, e.g. 100.00");
        if (amount == null) {
            return cancelCommand("TRANSFER");
        }

        // TRANSFER <fromAccount> <toAccount> <amount>
        return "TRANSFER " + from + " " + to + " " + amount;
    }

    private String buildInteractiveOfferLoanCommand() throws IOException {
        String from = promptForOrExit("account to offer the loan from",
                "existing account, e.g. Main");
        if (from == null) {
            return cancelCommand("OFFERLOAN");
        }

        String amount = promptForOrExit("loan amount",
                "numeric, e.g. 500.00");
        if (amount == null) {
            return cancelCommand("OFFERLOAN");
        }

        String rate = promptForOrExit("interest rate",
                "numeric percent, e.g. 5.5");
        if (rate == null) {
            return cancelCommand("OFFERLOAN");
        }

        String term = promptForOrExit("term in months",
                "whole number, e.g. 12");
        if (term == null) {
            return cancelCommand("OFFERLOAN");
        }

        // OFFERLOAN <fromAccount> <amount> <rate> <termMonths>
        return "OFFERLOAN " + from + " " + amount + " " + rate + " " + term;
    }

    private String buildInteractiveRequestLoanCommand() throws IOException {
        String to = promptForOrExit("account to receive the requested loan",
                "existing account, e.g. Main");
        if (to == null) {
            return cancelCommand("REQUESTLOAN");
        }

        String amount = promptForOrExit("loan amount",
                "numeric, e.g. 500.00");
        if (amount == null) {
            return cancelCommand("REQUESTLOAN");
        }

        String maxRate = promptForOrExit("maximum interest rate you will accept",
                "numeric percent, e.g. 5.5");
        if (maxRate == null) {
            return cancelCommand("REQUESTLOAN");
        }

        String term = promptForOrExit("term in months",
                "whole number, e.g. 12");
        if (term == null) {
            return cancelCommand("REQUESTLOAN");
        }

        // REQUESTLOAN <toAccount> <amount> <maxRate> <termMonths>
        return "REQUESTLOAN " + to + " " + amount + " " + maxRate + " " + term;
    }

    // Interactive CREATEACCOUNT flow:
    // Ask for account name
    // Confirm with YES, or EXIT to cancel

    private String buildInteractiveCreateAccountCommand() throws IOException {
        String name = promptForOrExit("name for the new account",
                "e.g. Savings");
        if (name == null) {
            return cancelCommand("CREATEACCOUNT");
        }

        String trimmedName = name.trim();
        if (trimmedName.isEmpty()) {
            System.out.println("Account name cannot be empty.");
            return cancelCommand("CREATEACCOUNT");
        }

        boolean confirmed = confirmYesOrExit(
                "Confirm a new account with the name '" + trimmedName
                        + "' by typing YES (or type EXIT to cancel):");
        if (!confirmed) {
            return cancelCommand("CREATEACCOUNT");
        }

        // CREATEACCOUNT <accountName>
        return "CREATEACCOUNT " + trimmedName;
    }

    private boolean readMultilineResponseUntilEndMarker(String endMarker) {
        try {
            while (true) {
                String line = connection.receive();
                if (line == null) {
                    System.out.println("Connection closed by server.");
                    return false;   // signal: server disconnected
                }
                if (line.equals(endMarker)) {
                    return true;    // signal: finished reading normally
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Connection closed by server.");
            return false;           // signal: server disconnected due to error
        }
    }
}