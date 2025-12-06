package newbank.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParser {

    public ParsedCommand parse(String input) {
        String trimmed = (input == null) ? "" : input.trim();

        if (trimmed.isEmpty()) {
            return ParsedCommand.invalid(
                    "Please enter a command. Type HELP for a list of commands.");
        }

        String[] tokens = trimmed.split("\\s+");
        String name = tokens[0].toUpperCase();

        List<String> args = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));

        // Validate argument count for known commands
        int expected = expectedArgumentCount(name);
        if (expected == -1) {
            // Unknown command at client side
            return ParsedCommand.invalid(
                    "Unknown command '" + name + "'. Type HELP for a list of commands.");
        }
        if (expected >= 0 && args.size() != expected) {
            String usage = usageFor(name);
            String message = "Invalid number of arguments for " + name + "."
                    + (usage.isEmpty() ? "" : " Usage: " + usage);
            return ParsedCommand.invalid(message);
        }

        return ParsedCommand.valid(name, args);
    }

    private int expectedArgumentCount(String name) {
        return switch (name) {
            // General / existing commands
            case "HELP" -> 0;
            case "SHOWMYACCOUNTS" -> 0;
            case "BALANCE", "BALANCES" -> 0; // aliases for SHOWMYACCOUNTS
            case "LOGOUT", "EXIT", "QUIT" -> 0;
            case "CREATEACCOUNT" -> 1;       // CREATEACCOUNT <accountName>
            case "CLOSEACCOUNT" -> 1;        // CLOSEACCOUNT <accountName>
            case "TRANSFER" -> 3;            // TRANSFER <from> <to> <amount>
            case "VIEWTRANSACTIONS" -> 1;    // VIEWTRANSACTIONS <accountName>

            // Loan commands
            case "OFFERLOAN" -> 4;           // OFFERLOAN <fromAcc> <amount> <rate> <termMonths>
            case "REQUESTLOAN" -> 4;         // REQUESTLOAN <toAcc> <amount> <maxRate> <termMonths>
            case "SHOWAVAILABLELOANS" -> 0;  // SHOWAVAILABLELOANS
            case "ACCEPTLOAN" -> 2;          // ACCEPTLOAN <loanId> <toAcc>
            case "MYLOANS" -> 0;             // MYLOANS
            case "REPAYLOAN" -> 2;           // REPAYLOAN <loanId> <amount>
            case "SHOWNOTIFICATIONS" ->  0;

            default -> -1;

        };
    }

    private String usageFor(String name) {
        return switch (name) {
            case "HELP" -> "HELP";
            case "SHOWMYACCOUNTS" -> "SHOWMYACCOUNTS";
            case "BALANCE" -> "BALANCE";
            case "LOGOUT", "EXIT", "QUIT" -> "LOGOUT / EXIT / QUIT";
            case "CREATEACCOUNT" -> "CREATEACCOUNT <accountName>";
            case "CLOSEACCOUNT" -> "CLOSEACCOUNT <accountName>";
            case "TRANSFER" -> "TRANSFER <fromAccount> <toAccount> <amount>";
            case "VIEWTRANSACTIONS" -> "VIEWTRANSACTIONS <accountName>";
            case "OFFERLOAN" -> "OFFERLOAN <fromAccount> <amount> <rate> <termMonths>";
            case "REQUESTLOAN" -> "REQUESTLOAN <toAccount> <amount> <maxRate> <termMonths>";
            case "SHOWAVAILABLELOANS" -> "SHOWAVAILABLELOANS";
            case "ACCEPTLOAN" -> "ACCEPTLOAN <loanId> <toAccount>";
            case "MYLOANS" -> "MYLOANS";
            case "REPAYLOAN" -> "REPAYLOAN <loanId> <amount>";
            default -> "";
        };
    }
}
