package newbank.server;

import newbank.server.loan.Loan;

import java.util.List;
import java.util.Arrays;
import newbank.server.notification.Notification;

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

                if(args.size() !=4){

                    return "Usage: OFFERLOAN <fromAccount> <amount> <rate> <termMonths>";
                }

                try {

                    String fromAcc = args.get(0);
                    double amountLoan = Double.parseDouble(args.get(1));
                    double rate = Double.parseDouble(args.get(2));
                    int months = Integer.parseInt(args.get(3));

                    //mock extra terms
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
