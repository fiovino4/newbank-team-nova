package newbank.server.service;

import newbank.server.model.Account;
import newbank.server.model.CustomerID;

import java.util.*;

public class AccountService {

    // username -> list of accounts
    private final Map<String, List<Account>> accountsByCustomer = new HashMap<>();

    private final CustomerService customerService;

    public AccountService(CustomerService customerService) {
        this.customerService = customerService;
    }

    private List<Account> getAccountListForUser(String username) {
        // ensure customer exists
        customerService.getOrThrow(username);
        return accountsByCustomer.computeIfAbsent(username, u -> new ArrayList<>());
    }

    private Account findAccount(String username, String accountName) {
        List<Account> accounts = accountsByCustomer.get(username);
        if (accounts == null) {
            return null;
        }
        for (Account a : accounts) {
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                return a;
            }
        }
        return null;
    }

    private Account getOrThrow(String username, String accountName) {
        Account acc = findAccount(username, accountName);
        if (acc == null) {
            throw new IllegalArgumentException("Account '" + accountName +
                    "' not found for customer '" + username + "'");
        }
        return acc;
    }

    // --- CRUD operations (by username) ---

    public boolean hasAccount(String username, String accountName) {
        return findAccount(username, accountName) != null;
    }

    public void addAccount(String username, Account account) {
        List<Account> accounts = getAccountListForUser(username);
        accounts.add(account);
    }

    public boolean removeAccount(String username, String accountName) {
        List<Account> accounts = accountsByCustomer.get(username);
        if (accounts == null) {
            return false;
        }
        return accounts.removeIf(a -> a.getAccountName().equalsIgnoreCase(accountName));
    }

    public String accountsToString(String username) {
        List<Account> accounts = accountsByCustomer.get(username);
        if (accounts == null || accounts.isEmpty()) {
            return "No accounts found.";
        }

        StringBuilder sb = new StringBuilder();
        for (Account a : accounts) {
            if (sb.length() != 0) {
                sb.append(System.lineSeparator());
            }
            sb.append("> ").append(a.toString());
        }
        return sb.toString();
    }

    // --- API using CustomerID (for commands etc.) ---

    public String showAccounts(CustomerID customerID) {
        return accountsToString(customerID.getKey());
    }

    public Account getAccount(CustomerID customerID, String accountName) {
        return findAccount(customerID.getKey(), accountName);
    }

    public void deposit(CustomerID customerID, String accountName, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        String username = customerID.getKey();
        Account account = getOrThrow(username, accountName);
        account.setBalance(account.getBalance() + amount);
    }

    public void withdraw(CustomerID customerID, String accountName, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        String username = customerID.getKey();
        Account account = getOrThrow(username, accountName);
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        account.setBalance(account.getBalance() - amount);
    }

    public void transfer(CustomerID customerID, String fromAccount, String toAccount, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        String username = customerID.getKey();

        Account from = getOrThrow(username, fromAccount);
        Account to   = getOrThrow(username, toAccount);

        if (from.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
    }
}
