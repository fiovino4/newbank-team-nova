package newbank.tests;

import newbank.server.model.Account;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountTest {

    @Test
    public void shouldStoreNameAndOpeningBalance() {
        Account account = new Account("Savings", 200.0);

        String name = account.getAccountName();
        double balance = account.getBalance();

        assertEquals("Account name should match constructor argument",
                "Savings", name);
        assertEquals("Account balance should match constructor argument",
                200.0, balance, 0.0001);
    }

    @Test
    public void shouldAllowZeroOpeningBalance() {
        Account account = new Account("Empty", 0.0);

        String name = account.getAccountName();
        double balance = account.getBalance();

        assertEquals("Account name should be 'Empty'",
                "Empty", name);
        assertEquals("Account balance should be zero when constructed with 0.0",
                0.0, balance, 0.0001);
    }

    @Test
    public void toStringShouldIncludeNameAndBalance() {
        Account account = new Account("Checking", 150.0);

        String s = account.toString();

        assertTrue("toString() should contain the account name",
                s.contains("Checking"));
        assertTrue("toString() should contain the balance (150.0)",
                s.contains("150"));
    }
}