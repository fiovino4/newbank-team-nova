package newbank.tests;

import newbank.server.Account;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    @Test
    public void shouldReturnCorrectToString() {
        Account account = new Account("Savings", 200.0);
        assertEquals("Savings: 200.0", account.toString());
    }
    // Verifies that the toString method returns the correct format.

    @Test
    public void shouldStoreAccountNameAndOpeningBalance() {
        Account account = new Account("Checking", 150.0);
        // Direct field access is not possible, so test via toString
        assertTrue(account.toString().contains("Checking"));
        assertTrue(account.toString().contains("150.0"));
    }
    // Checks that the account name and opening balance are stored correctly.
}