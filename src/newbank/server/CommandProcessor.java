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

            case "CREATEACCOUNT": {
                if (args.size() != 1) {
                    return "Usage: CREATEACCOUNT <accountName>";
                }

                String accountName = args.get(0);
                boolean created = bank.createAccount(customer, accountName);
                if (created) {
                    return "SUCCESS: Account '" + accountName + "' created.";
                } else {
                    return "FAIL: Could not create account '" + accountName
                            + "'. It may already exist.";
                }
            }

            case "CLOSEACCOUNT": {
                if (args.size() != 1) {
                    return "Usage: CLOSEACCOUNT <accountName>";
                }

                String closeAccountName = args.get(0);
                return bank.closeAccount(customer, closeAccountName);
            }

            case "TRANSFER": {
                if (args.size() != 3) {
                    return "Usage: TRANSFER <fromAccount> <toAccount> <amount>";
                }

                String fromAccount = args.get(0);
                String toAccount   = args.get(1);
                String amountStr   = args.get(2);

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    return "FAIL: Amount must be a number. Usage: TRANSFER <fromAccount> <toAccount> <amount>";
                }

                if (amount <= 0) {
                    return "FAIL: Amount must be positive.";
                }

                return bank.transfer(customer, fromAccount, toAccount, amount);
            }

            case "OFFERLOAN": {
                // Usage: OFFERLOAN <fromAccount> <amount> <annualRate%> <termMonths> [extra terms...]
                if (args.size() < 4) {
                    return "Usage: OFFERLOAN <fromAccount> <amount> <annualRate%> <termMonths> [extra terms...]";
                }

                String fromAccount = args.get(0);
                String amountStr   = args.get(1);
                String rateStr     = args.get(2);
                String termStr     = args.get(3);

                String extraTerms = "";
                if (args.size() > 4) {
                    extraTerms = String.join(" ", args.subList(4, args.size()));
                }

                double amount;
                double annualRate;
                int termMonths;

                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    return "FAIL: Amount must be a number.";
                }

                try {
                    annualRate = Double.parseDouble(rateStr);
                } catch (NumberFormatException e) {
                    return "FAIL: Interest rate must be a number (e.g. 5 or 5.5).";
                }

                try {
                    termMonths = Integer.parseInt(termStr);
                } catch (NumberFormatException e) {
                    return "FAIL: Term must be an integer number of months.";
                }

                return bank.offerLoan(customer, fromAccount, amount, annualRate, termMonths, extraTerms);
            }

            case "REQUESTLOAN":
            case "SHOWAVAILABLELOANS": {
                return bank.showAvailableLoans();
                }

                case "MYLOANS": {
                    return bank.showMyLoans(customer);
                }
                            case "ACCEPTLOAN":

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
                "  OFFERLOAN <fromAccount> <amount> <annualRate%> <termMonths> [extra terms...]",
                "  SHOWAVAILABLELOANS",
                "  MYLOANS",

                // more loan commands later...
                "  LOGOUT / EXIT / QUIT"
        );
    }
}
