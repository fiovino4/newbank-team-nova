package newbank.tests;

import newbank.server.NewBank;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewBankLoginTest {

    private final NewBank bank = NewBank.getBank();

    // -------------------------
    // LM-104 - Login validation
    // -------------------------

    @Test
    public void validUsersShouldSuccessfullyLogIn() {
        assertNotNull(bank.checkLogInDetails("Bhagy", "1234"));
        assertNotNull(bank.checkLogInDetails("Christina", "abcd"));
        assertNotNull(bank.checkLogInDetails("John", "pass"));
        assertNotNull(bank.checkLogInDetails("Test", "Test"));
    }

    @Test
    public void unknownUsersShouldBeRejected() {
        assertNull(bank.checkLogInDetails("GhostUser_12345", "anything"));
    }

    @Test
    public void incorrectPasswordsShouldBeRejected() {
        assertNull(bank.checkLogInDetails("Bhagy", "wrong"));
        assertNull(bank.checkLogInDetails("Christina", "wrong"));
        assertNull(bank.checkLogInDetails("John", "wrong"));
        assertNull(bank.checkLogInDetails("Test", "wrong"));
    }

    @Test
    public void usernameIsCaseSensitive() {
        assertNull(bank.checkLogInDetails("bhagy", "1234"));
        assertNull(bank.checkLogInDetails("CHRISTINA", "abcd"));
        assertNull(bank.checkLogInDetails("john", "pass"));
    }

    @Test
    public void blankAndWhitespaceInputsShouldBeRejected() {
        assertNull(bank.checkLogInDetails("", "1234"));
        assertNull(bank.checkLogInDetails("   ", "1234"));
        assertNull(bank.checkLogInDetails("Bhagy", ""));
        assertNull(bank.checkLogInDetails("Bhagy", "   "));
    }
}