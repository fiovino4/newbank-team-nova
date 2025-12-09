package newbank.tests;

import newbank.server.Account;
import newbank.server.Customer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @Test
    public void shouldAddAccountToCustomer() {
        Customer customer = new Customer();
        Account account = new Account("Savings", 200.0);
        customer.addAccount(account);
        String accountsString = customer.accountsToString();
        assertTrue(accountsString.contains("Savings"));
        assertTrue(accountsString.contains("200.0"));
    }
    // Verifies that an account can be added to a customer and is reflected in the accounts string.

    @Test
    public void accountsToStringShouldReturnEmptyStringIfNoAccounts() {
        Customer customer = new Customer();
        assertEquals("", customer.accountsToString());
    }
    // Checks that accountsToString returns an empty string when the customer has no accounts.
}