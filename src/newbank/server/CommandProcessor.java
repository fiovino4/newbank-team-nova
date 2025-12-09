package newbank.server;

import java.util.List;
import java.util.Arrays;

import newbank.server.model.Account;
import newbank.server.model.CustomerID;
import newbank.server.model.Loan;
import newbank.server.model.Notification;

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
            case "BALANCE":
            case "BALANCES": {
                // Use AccountService via NewBank
                String accounts = bank.getAccountService().showAccounts(customer);
                return accounts + "\nEND_OF_ACCOUNTS";
            }

            case "CREATEACCOUNT": {
                if (args.size() != 1) {
                    return "Usage: CREATEACCOUNT <accountName>";
                }

                String accountName = args.get(0);
                String username = customer.getKey();

                // Use CustomerService to check + create
                if (bank.getAccountService().hasAccount(username, accountName)) {
                    return "FAIL: Could not create account '" + accountName
                            + "'. It may already exist.";
                }

                // Starting balance 0.0 by default
                bank.getAccountService().addAccount(username, new Account(accountName, 0.0));

                return "SUCCESS: Account '" + accountName + "' created.";
            }

            case "CLOSEACCOUNT": {
                if (args.size() != 1) {
                    return "Usage: CLOSEACCOUNT <accountName>";
                }

                String closeAccountName = args.get(0);
                String username = customer.getKey();

                boolean removed = bank.getAccountService().removeAccount(username, closeAccountName);
                if (removed) {
                    return "SUCCESS: Account '" + closeAccountName + "' closed.";
                } else {
                    return "FAIL: Account '" + closeAccountName + "' not found or could not be closed.";
                }
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

                try {
                    bank.getAccountService().transfer(customer, fromAccount, toAccount, amount);
                    return "SUCCESS: Transferred " + amount + " from '" + fromAccount +
                            "' to '" + toAccount + "'.";
                } catch (IllegalArgumentException e) {
                    return "FAIL: " + e.getMessage();
                }
            }

            case "OFFERLOAN":

                if(args.size() !=4){

                    return "Usage: OFFERLOAN <fromAccount> <amount> <rate> <termMonths>";
                }

                try {

                    String fromAcc = args.get(0);
                    double amountLoan = Double.parseDouble(args.get(1));
                    double rate = Double.parseDouble(args.get(2));
                    int months = Integer.parseInt(args.get(3));

                    Loan loan = bank.getLoanService().offerLoan(customer, fromAcc, amountLoan, rate, months, "");

                    return "SUCCESS: Loan created with ID " + loan.getId();

                }catch (NumberFormatException e){

                    return "FAIL: amount or rate or termMonths must be numeric.";
                }catch (IllegalArgumentException e){

                    return "FAIL: " + e.getMessage();
                }

            case "REQUESTLOAN":

                if(args.size() != 1){
                    return "Usage: REQUESTLOAN <loanId>";
                }

                try {
                    int requestedLoanId = Integer.parseInt(args.get(0));
                    Loan loan = bank.getLoanService().requestLoan(customer, requestedLoanId);

                    return "SUCCESS: Loan " + loan.getId() + " has been successfully requested.";

                }catch (IllegalArgumentException e){

                    return "FAIL: " + e.getMessage();
                }

            case "SHOWNOTIFICATIONS": {
                List<Notification> notifications = bank.getNotificationService().getNotifications(customer);

                if (notifications.isEmpty()) {
                    return "You have no notifications.";
                }

                StringBuilder sb = new StringBuilder("Your notifications:\n");
                for (Notification n : notifications) {
                    sb.append("  [").append(n.getId()).append("] ")
                            .append(n.getMessage()).append(" (").append(n.isRead() ? "read" : "unread")
                            .append(")\n");
                }
                return sb.toString().trim();
            }

            case "SHOWAVAILABLELOANS": {
                String result = bank.getLoanService().showAvailableLoans();

                return result + "END_OF_LOANS";
            }

            case "MYLOANS": {
                String result = bank.getLoanService().showUserLoan(customer);

                return result + System.lineSeparator() + "END_OF_MYLOANS";
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
                "  You can either:",
                "    - Type the full command with arguments, e.g. TRANSFER Main Savings 100.00",
                "    - Or type just the command name (e.g. TRANSFER, CREATEACCOUNT) and follow the prompts.",
                "",
                "  SHOWMYACCOUNTS",
                "  BALANCE",
                "  CREATEACCOUNT <accountName>",
                "  CLOSEACCOUNT <accountName>",
                "  TRANSFER <fromAccount> <toAccount> <amount>",
                "  VIEWTRANSACTIONS <accountName>",
                "  OFFERLOAN <fromAccount> <amount> <annualRate%> <termMonths> [extra terms...]",
                "  REQUESTLOAN <loanId>",
                "  SHOWAVAILABLELOANS",
                "  ACCEPTLOAN <loanId> <toAccount>",
                "  MYLOANS",
                "  REPAYLOAN <loanId> <amount>",
                "  LOGOUT / EXIT / QUIT",
                "END_OF_HELP"
        );
    }
}
