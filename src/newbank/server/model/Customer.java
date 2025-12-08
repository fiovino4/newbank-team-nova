package newbank.server.model;

import newbank.server.service.security.PasswordManagerService;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private final List<Account> accounts;
    private final String passwordHash;
    private final String passwordSalt;

    public Customer(String userInputPassword) {
        try {
            PasswordManagerService.HashResult result =
                    PasswordManagerService.hashPassword(userInputPassword);

            this.passwordHash = result.hash;
            this.passwordSalt = result.salt;

        } catch (Exception e) {
            throw new RuntimeException("Error hashing password process", e);
        }

        this.accounts = new ArrayList<>();
    }

    public boolean checkPassword(String passwordToCheck) {
        try {
            return PasswordManagerService.verify(passwordToCheck, passwordHash, passwordSalt);
        } catch (Exception e) {
            throw new RuntimeException("Password verification failed", e);
        }
    }

    public String accountsToString() {
        StringBuilder sb = new StringBuilder();
        for (Account a : accounts) {
            if (sb.length() != 0) { // instead of sb.isEmpty()
                sb.append(System.lineSeparator());
            }
            sb.append("> ").append(a.toString());
        }
        return sb.toString();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Account getAccountByName(String name) {
        for (Account a : accounts) {
            if (a.getAccountName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }
}
