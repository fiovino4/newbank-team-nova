package newbank.server;

import java.util.List;
import java.util.Arrays;

public class CommandProcessor {

    private final NewBank bank;

    public CommandProcessor(NewBank bank) {
        this.bank = bank;
    }

    public String process(CustomerID customer, String request) {
        if (request == null || request.trim().isEmpty()) {
            return "FAIL: Empty command.";
        }

        if (!bank.hasCustomer(customer)) {
            return "FAIL: Unknown customer.";
        }

        String[] tokens = request.trim().split("\\s+");
        String name = tokens[0].toUpperCase();
        List<String> args = Arrays.asList(tokens).subList(1, tokens.length);

        switch (name) {

            case "HELP":
                return buildHelpMessage();

            case "LOGOUT":
            case "EXIT":
            case "QUIT":
                return "Session terminated. Goodbye.";


            case "SHOWMYACCOUNTS":
                return bank.showMyAccounts(customer);

            case "CREATEACCOUNT":
                if (args.size() != 1) {
                    return "Usage: CREATEACCOUNT <accountName>";
                }
                // This needs development
                return "CREATEACCOUNT not implemented yet.";

            case "CLOSEACCOUNT":
                if (args.size() != 1) {
                    return "Usage: CLOSEACCOUNT <accountName>";
                }
                return "CLOSEACCOUNT not implemented yet.";

            case "TRANSFER":
                if (args.size() != 3) {
                    return "Usage: TRANSFER <fromAccount> <toAccount> <amount>";
                }
//                Commented out for now
//                String from = args.get(0);
//                String to   = args.get(1);
                String amountStr = args.get(2);

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    return "FAIL: Amount must be a number. Usage: TRANSFER <fromAccount> <toAccount> <amount>";
                }

                if (amount <= 0) {
                    return "FAIL: Amount must be positive.";
                }

                // This needs development
                return "TRANSFER not implemented yet.";

            // ===== Loans (placeholders) =====
            case "OFFERLOAN":
            case "REQUESTLOAN":
            case "SHOWAVAILABLELOANS":
            case "ACCEPTLOAN":
            case "MYLOANS":
            case "REPAYLOAN":
                return name + " not implemented yet on server side.";

            default:
                return "FAIL: Unknown command '" + name + "'. Type HELP for available commands.";
        }
    }

    private String buildHelpMessage() {
        return String.join("\n",
                "Available commands:",
                "  SHOWMYACCOUNTS",
                "  CREATEACCOUNT <accountName>",
                "  CLOSEACCOUNT <accountName>",
                "  TRANSFER <fromAccount> <toAccount> <amount>",
                "  VIEWTRANSACTIONS <accountName>",

//                "  OFFERLOAN <fromAccount> <amount> <rate> <termMonths>",
//                "  REQUESTLOAN <toAccount> <amount> <maxRate> <termMonths>",
//                "  SHOWAVAILABLELOANS",
//                "  ACCEPTLOAN <loanId> <toAccount>",
//                "  MYLOANS",
//                "  REPAYLOAN <loanId> <amount>",
//                "",
                "  LOGOUT / EXIT / QUIT"
        );
    }
}
