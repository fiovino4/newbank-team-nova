package newbank.tests;

import newbank.server.model.Customer;
import org.junit.Test;

import static org.junit.Assert.*;

public class CustomerTest {

    private void assertPasswordCheck(String expectedDescription,
                                     boolean expected,
                                     Customer customer,
                                     String passwordToCheck) {

        boolean actual = customer.checkPassword(passwordToCheck);
        assertEquals(
                "Password check failed: " + expectedDescription +
                        " (password tested: '" + passwordToCheck + "')",
                expected,
                actual
        );
    }

    @Test
    public void shouldAcceptCorrectPassword() {
        Customer customer = new Customer("NewNewAccount");

        assertPasswordCheck(
                "expected correct password to be accepted",
                true,
                customer,
                "NewNewAccount"
        );
    }
    // Verifies that checkPassword returns true for the original password.

    @Test
    public void shouldRejectWrongPassword() {
        Customer customer = new Customer("NewNewAccount");

        assertPasswordCheck(
                "expected wrong password to be rejected",
                false,
                customer,
                "WrongPassword"
        );
    }
    // Verifies that checkPassword returns false for an incorrect password.
}