package newbank.server.service;

import newbank.server.model.Customer;

import java.util.HashMap;
import java.util.Map;

public class CustomerService {

    private final Map<String, Customer> customers = new HashMap<>();

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

    public boolean hasCustomer(String username) {
        return customers.containsKey(username);
    }

    // --- helper (if you still want a throwing getter) ---

    public Customer getOrThrow(String username) {
        Customer customer = customers.get(username);
        if (customer == null) {
            throw new IllegalArgumentException("Unknown customer '" + username + "'");
        }
        return customer;
    }
}
