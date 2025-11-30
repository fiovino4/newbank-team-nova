package newbank.server;

import java.util.HashMap;

public class NewBank {

    private static final NewBank bank = new NewBank();
    private final HashMap<String, Customer> customers;

    private NewBank() {
        customers = new HashMap<>();
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


}
