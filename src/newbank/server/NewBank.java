package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewBank {

    private static final NewBank bank = new NewBank();
    private final HashMap<String, Customer> customers;
     // NEW: store loan offers
    private final List<Loan> loanOffers;

    private NewBank() {
        customers = new HashMap<>();
        loanOffers = new ArrayList<>();
        addTestData();
    }

    private void addTestData() {
        Customer bhagy = new Customer("1234");
        bhagy.addAccount(new Account("Main", 1000.0));
        customers.put("Bhagy", bhagy);

        Customer christina = new Customer("abcd");
        christina.addAccount(new Account("Savings", 1500.0));
        customers.put("Christina", christina);

        Customer john = new Customer("pass");
        john.addAccount(new Account("Checking", 250.0));
        customers.put("John", john);
      
        Customer test = new Customer("Test");
        test.addAccount(new Account("Main", 1000.0));
        test.addAccount(new Account("Savings", 1000.0));
        test.addAccount(new Account("Bonds", 1000.0));
        customers.put("Test", test);
    }

    public static NewBank getBank() {
        return bank;
    }

    public Customer getCustomer(String userName) {
        return customers.get(userName);
    }

    public boolean hasCustomer(CustomerID customerID) {
        return customers.containsKey(customerID.getKey());
    }
  
    public String showMyAccounts(CustomerID customerID) {
        Customer c = customers.get(customerID.getKey());
        if (c == null) {
            return "FAIL: Unknown customer.";
        }
        return c.accountsToString();
    }

    public synchronized CustomerID checkLogInDetails(String userName, String password) {
        if (customers.containsKey(userName)) {
            Customer customer = customers.get(userName);

            if (customer.checkPassword(password)) {
                return new CustomerID(userName);
            } else {
                return null;   // incorrect password
            }
        }
        return null;
    }

    public synchronized boolean createAccount(CustomerID customerID, String accountName) {
    Customer customer = customers.get(customerID.getKey());
    if (customer == null) {
        return false; // unknown customer
    }

    // don’t create duplicates
    if (customer.hasAccount(accountName)) {
        return false;
    }

    customer.addAccount(new Account(accountName, 0.0)); // or whatever initial balance
    return true;
}


public synchronized String transfer(CustomerID customerID,
                                    String fromAccountName,
                                    String toAccountName,
                                    double amount) {
    Customer customer = customers.get(customerID.getKey());
    if (customer == null) {
        return "FAIL: Unknown customer.";
    }

    if (fromAccountName.equalsIgnoreCase(toAccountName)) {
        return "FAIL: From and to accounts must be different.";
    }

    Account from = customer.getAccount(fromAccountName);
    if (from == null) {
        return "FAIL: From-account '" + fromAccountName + "' not found.";
    }

    Account to = customer.getAccount(toAccountName);
    if (to == null) {
        return "FAIL: To-account '" + toAccountName + "' not found.";
    }

    if (from.getBalance() < amount) {
        return "FAIL: Insufficient funds in '" + fromAccountName + "'.";
    }

    // do the transfer
    from.setBalance(from.getBalance() - amount);
    to.setBalance(to.getBalance() + amount);

    return "SUCCESS: Transferred " + amount +
           " from '" + fromAccountName + "' to '" + toAccountName + "'.";
}

public synchronized String closeAccount(CustomerID customerID, String accountName) {
    Customer customer = customers.get(customerID.getKey());
    if (customer == null) {
        return "FAIL: Unknown customer.";
    }

    Account account = customer.getAccount(accountName);
    if (account == null) {
        return "FAIL: Account '" + accountName + "' not found.";
    }

    // Simple rule: only allow closing if balance is zero
    if (account.getBalance() != 0.0) {
        return "FAIL: Cannot close account '" + accountName +
               "' because its balance is not zero (" + account.getBalance() + ").";
    }

    boolean removed = customer.removeAccount(accountName);
    if (!removed) {
        return "FAIL: Could not close account '" + accountName + "'.";
    }

    return "SUCCESS: Account '" + accountName + "' closed.";
}

public synchronized String offerLoan(CustomerID customerID,
                                     String fromAccountName,
                                     double amount,
                                     double annualRate,
                                     int termMonths,
                                     String extraTerms) {
    Customer customer = customers.get(customerID.getKey());
    if (customer == null) {
        return "FAIL: Unknown customer.";
    }

    if (amount <= 0) {
        return "FAIL: Loan amount must be positive.";
    }
    if (annualRate <= 0) {
        return "FAIL: Interest rate must be positive.";
    }
    if (termMonths <= 0) {
        return "FAIL: Term must be a positive number of months.";
    }

    Account from = customer.getAccount(fromAccountName);
    if (from == null) {
        return "FAIL: Account '" + fromAccountName + "' not found.";
    }

    // Optional rule: lender must have at least this much balance
    if (from.getBalance() < amount) {
        return "FAIL: Insufficient funds in '" + fromAccountName +
               "' to offer a loan of " + amount + ".";
    }

    Loan loan = new Loan(
            customerID.getKey(),
            fromAccountName,
            amount,
            annualRate,
            termMonths,
            extraTerms
    );

    loanOffers.add(loan);

    return "SUCCESS: Created loan offer #" + loan.getId() +
           " from account '" + fromAccountName + "'.";
}

// List all OPEN loan offers in the marketplace
public synchronized String showAvailableLoans() {
    if (loanOffers.isEmpty()) {
        return "No loan offers available.";
    }

    StringBuilder sb = new StringBuilder();
    boolean anyOpen = false;

    for (Loan loan : loanOffers) {
        if ("OPEN".equalsIgnoreCase(loan.getStatus())) {
            if (!anyOpen) {
                sb.append("Available loan offers:\n");
                anyOpen = true;
            } else {
                sb.append(System.lineSeparator());
            }
            sb.append(loan.toString());
        }
    }

    if (!anyOpen) {
        return "No open loan offers available.";
    }

    return sb.toString();
}

// List loan offers created by the current customer
public synchronized String showMyLoans(CustomerID customerID) {
    if (customerID == null) {
        return "FAIL: Not logged in.";
    }

    String customerName = customerID.getKey();
    StringBuilder sb = new StringBuilder();

    for (Loan loan : loanOffers) {
        if (loan.getLenderCustomerName().equalsIgnoreCase(customerName)) {
            if (sb.length() == 0) {
                sb.append("Your loan offers:\n");
            } else {
                sb.append(System.lineSeparator());
            }
            sb.append(loan.toString());
        }
    }

    if (sb.length() == 0) {
        return "You have not created any loan offers.";
    }

    return sb.toString();
}

}