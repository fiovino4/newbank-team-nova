package newbank.tests;

import newbank.server.NewBank;
import newbank.server.model.CustomerID;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewBankTest {

    @Test
    public void shouldReturnSingletonInstance() {
        NewBank bank1 = NewBank.getBank();
        NewBank bank2 = NewBank.getBank();

        assertSame("NewBank should be a singleton", bank1, bank2);
    }
    // Verifies that NewBank follows the singleton pattern.

    @Test
    public void shouldRecogniseExistingCustomer() {
        NewBank bank = NewBank.getBank();

        // "Bhagy" is added in addTestData()
        CustomerID customerID = new CustomerID("Bhagy");

        assertTrue("NewBank should recognise existing customer 'Bhagy'",
                bank.hasCustomer(customerID));
    }
    // Checks that hasCustomer reports true for a known customer.

    @Test
    public void shouldCheckLogInDetailsForExistingUser() {
        NewBank bank = NewBank.getBank();

        // "Bhagy" is registered with password "1234" in addTestData()
        CustomerID customerId = bank.checkLogInDetails("Bhagy", "1234");

        assertNotNull("Valid login for 'Bhagy' with correct password should return a CustomerID",
                customerId);
        assertEquals("CustomerID should be for 'Bhagy'",
                "Bhagy", customerId.getKey());
    }
    // Checks that valid login details return a CustomerID.

    @Test
    public void shouldReturnNullForNonexistentUserLogIn() {
        NewBank bank = NewBank.getBank();

        CustomerID customerId = bank.checkLogInDetails("NonexistentUser", "whatever");

        assertNull("Login for unknown user should return null", customerId);
    }
    // Ensures that invalid login details return null.

    @Test
    public void shouldShowAccountsForValidCustomer() {
        NewBank bank = NewBank.getBank();

        // Log in as Bhagy with the correct password from addTestData()
        CustomerID customerId = bank.checkLogInDetails("Bhagy", "1234");
        assertNotNull("Expected valid CustomerID for 'Bhagy'", customerId);

        String accounts = bank.showMyAccounts(customerId);
        assertNotNull("SHOWMYACCOUNTS output should not be null", accounts);

        // From addTestData(): Bhagy has account "Main" with 1000.0
        assertTrue("Accounts should contain the 'Main' account",
                accounts.contains("Main"));
        assertTrue("Accounts should contain the balance 1000.0",
                accounts.contains("1000.0"));
    }
    // Verifies that SHOWMYACCOUNTS returns the correct account information for a known customer.

    @Test
    public void shouldLoginSuccessfullyWithCorrectCredentials() {
        NewBank bank = NewBank.getBank();
        CustomerID id = bank.checkLogInDetails("Bhagy", "1234"); // Assuming "" is a valid password for test data
        assertNotNull(id);
        String response = bank.processRequest(id, "SHOWMYACCOUNTS");
        assertTrue(response.contains("Main"));
    }
    // Verifies successful login with correct credentials.
}