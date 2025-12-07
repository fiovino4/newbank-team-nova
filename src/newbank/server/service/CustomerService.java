package newbank.server.service;

import newbank.server.model.Account;
import newbank.server.model.Customer;

import java.util.HashMap;
import java.util.Map;

public class CustomerService {

    // Simple in-memory store: username -> Customer
    private final Map<String, Customer> customers = new HashMap<>();

    // --- Customer management ---

    public Customer registerCustomer(String username, String rawPassword) {
        if (customers.containsKey(username)) {
            throw new IllegalArgumentException("Customer '" + username + "' already exists.");
        }

        Customer customer = new Customer(rawPassword);
        customers.put(username, customer);
        return customer;
    }

    public boolean authenticate(String username, String password) {
        Customer customer = customers.get(username);
        if (customer == null) {
            return false;
        }
        return customer.checkPassword(password);
    }

    public Customer getCustomer(String username) {
        return customers.get(username);
    }

    // --- Account related methods (moved from Customer) ---

    public void addAccount(String username, Account account) {
        Customer customer = getOrThrow(username);
        customer.addAccount(account);
    }

    public boolean hasAccount(String username, String accountName) {
        Customer customer = getOrThrow(username);
        for (Account a : customer.getAccounts()) {
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                return true;
            }
        }
        return false;
    }

    public Account getAccount(String username, String accountName) {
        Customer customer = getOrThrow(username);
        for (Account a : customer.getAccounts()) {
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                return a;
            }
        }
        return null;
    }

    public boolean removeAccount(String username, String accountName) {
        Customer customer = getOrThrow(username);
        for (int i = 0; i < customer.getAccounts().size(); i++) {
            Account a = customer.getAccounts().get(i);
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                customer.getAccounts().remove(i);
                return true;
            }
        }
        return false;
    }

    public String accountsToString(String username) {
        Customer customer = getOrThrow(username);
        StringBuilder sb = new StringBuilder();
        for (Account a : customer.getAccounts()) {
            if (sb.length() != 0) {
                sb.append(System.lineSeparator());
            }
            sb.append("> ").append(a.toString());
        }
        return sb.toString();
    }

    // --- helper ---

    private Customer getOrThrow(String username) {
        Customer customer = customers.get(username);
        if (customer == null) {
            throw new IllegalArgumentException("Unknown customer '" + username + "'");
        }
        return customer;
    }
}
