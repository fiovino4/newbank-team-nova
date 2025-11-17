package newbank.client;

import java.util.ArrayList;
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

        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            args.add(tokens[i]);
        }

        // Validate argument count for known commands
        int expected = expectedArgumentCount(name);
        if (expected >= 0 && args.size() != expected) {
            String usage = usageFor(name);
            String message = "Invalid number of arguments for " + name + "."
                    + (usage.isEmpty() ? "" : " Usage: " + usage);
            return ParsedCommand.invalid(message);
        }

        return ParsedCommand.valid(name, args);
    }

    private int expectedArgumentCount(String name) {
        switch (name) {
            // General / existing commands
            case "HELP": return 0;
            case "SHOWMYACCOUNTS": return 0;
            case "CREATEACCOUNT": return 1;       // CREATEACCOUNT <accountName>
            case "CLOSEACCOUNT": return 1;        // CLOSEACCOUNT <accountName>
            case "TRANSFER": return 3;            // TRANSFER <from> <to> <amount>
            case "VIEWTRANSACTIONS": return 1;    // VIEWTRANSACTIONS <accountName>

            // Loan commands
            case "OFFERLOAN": return 4;           // OFFERLOAN <fromAcc> <amount> <rate> <termMonths>
            case "REQUESTLOAN": return 4;         // REQUESTLOAN <toAcc> <amount> <maxRate> <termMonths>
            case "SHOWAVAILABLELOANS": return 0;  // SHOWAVAILABLELOANS
            case "ACCEPTLOAN": return 2;          // ACCEPTLOAN <loanId> <toAcc>
            case "MYLOANS": return 0;             // MYLOANS
            case "REPAYLOAN": return 2;           // REPAYLOAN <loanId> <amount>

            default:
                // Unknown commands maybe handled server-side?
                return -1;
        }
    }

    private String usageFor(String name) {
        switch (name) {
            case "HELP":
                return "HELP";
            case "SHOWMYACCOUNTS":
                return "SHOWMYACCOUNTS";
            case "CREATEACCOUNT":
                return "CREATEACCOUNT <accountName>";
            case "CLOSEACCOUNT":
                return "CLOSEACCOUNT <accountName>";
            case "TRANSFER":
                return "TRANSFER <fromAccount> <toAccount> <amount>";
            case "VIEWTRANSACTIONS":
                return "VIEWTRANSACTIONS <accountName>";
            case "OFFERLOAN":
                return "OFFERLOAN <fromAccount> <amount> <rate> <termMonths>";
            case "REQUESTLOAN":
                return "REQUESTLOAN <toAccount> <amount> <maxRate> <termMonths>";
            case "SHOWAVAILABLELOANS":
                return "SHOWAVAILABLELOANS";
            case "ACCEPTLOAN":
                return "ACCEPTLOAN <loanId> <toAccount>";
            case "MYLOANS":
                return "MYLOANS";
            case "REPAYLOAN":
                return "REPAYLOAN <loanId> <amount>";
            default:
                return "";
        }
    }
}
