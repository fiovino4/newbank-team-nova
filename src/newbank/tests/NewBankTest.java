package newbank.tests;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NewBankTest {

    @Test
    public void shouldReturnSingletonInstance() {
        NewBank bank1 = NewBank.getBank();
        NewBank bank2 = NewBank.getBank();
        assertSame(bank1, bank2);
    }
    // Verifies that NewBank follows the singleton pattern.

    @Test
    public void shouldCheckLogInDetailsForExistingUser() {
        NewBank bank = NewBank.getBank();
        // "Bhagy" is added in addTestData()
        assertNotNull(bank.checkLogInDetails("Bhagy", ""));
    }
    // Checks that valid login details return a CustomerID.

    @Test
    public void shouldReturnNullForNonexistentUserLogIn() {
        NewBank bank = NewBank.getBank();
        assertNull(bank.checkLogInDetails("Nonexistent", ""));
    }
    // Ensures that invalid login details return null.

    @Test
    public void shouldShowAccountsForValidCustomer() {
        NewBank bank = NewBank.getBank();
        CustomerID customerId = bank.checkLogInDetails("Bhagy", "");
        String accounts = bank.processRequest(customerId, "SHOWMYACCOUNTS");
        assertTrue(accounts.contains("Main"));
        assertTrue(accounts.contains("1000.0"));
    }
    // Verifies that SHOWMYACCOUNTS returns the correct account information.

    @Test
    public void shouldFailForInvalidRequest() {
        NewBank bank = NewBank.getBank();
        CustomerID customerId = bank.checkLogInDetails("Bhagy", "");
        String response = bank.processRequest(customerId, "INVALIDREQUEST");
        assertEquals("FAIL", response);
    }
    // Checks that invalid requests return "FAIL".
}