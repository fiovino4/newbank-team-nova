package newbank.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class NewBankLoginAssertTest {

    public static void main(String[] args) {
        NewBank bank = getBankInstance();

        // -------------------------
        // LM-104 - Login validation
        // -------------------------

        // 1) Valid Users Must Successfully Log In (match seeded casing)
        assert bank.checkLogInDetails("Bhagy", "1234") != null
                : "Valid login failed for Bhagy";
        assert bank.checkLogInDetails("Christina", "abcd") != null
                : "Valid login failed for Christina";
        assert bank.checkLogInDetails("John", "pass") != null
                : "Valid login failed for John";
        assert bank.checkLogInDetails("Test", "Test") != null
                : "Valid login failed for Test";

        // 2) Unknown / Non-Existent Users Must Be Rejected
        assert bank.checkLogInDetails("GhostUser_12345", "anything") == null
                : "Unknown user was accepted";

        // 3) Incorrect Passwords Must Be Rejected
        assert bank.checkLogInDetails("Bhagy", "wrong") == null
                : "Wrong password accepted for Bhagy";
        assert bank.checkLogInDetails("Christina", "wrong") == null
                : "Wrong password accepted for Christina";
        assert bank.checkLogInDetails("John", "wrong") == null
                : "Wrong password accepted for John";
        assert bank.checkLogInDetails("Test", "wrong") == null
                : "Wrong password accepted for Test";

        // 4) Case Sensitivity Is Enforced
        // Wrong casing should behave like unknown user
        assert bank.checkLogInDetails("bhagy", "1234") == null
                : "Case sensitivity not enforced for Bhagy";
        assert bank.checkLogInDetails("CHRISTINA", "abcd") == null
                : "Case sensitivity not enforced for Christina";
        assert bank.checkLogInDetails("john", "pass") == null
                : "Case sensitivity not enforced for John";

        // 5) Blank or Whitespace Input
        assert bank.checkLogInDetails("", "1234") == null
                : "Blank username accepted";
        assert bank.checkLogInDetails("   ", "1234") == null
                : "Whitespace username accepted";
        assert bank.checkLogInDetails("Bhagy", "") == null
                : "Blank password accepted";
        assert bank.checkLogInDetails("Bhagy", "   ") == null
                : "Whitespace password accepted";

        System.out.println("LM-104: all login assertions passed.");
    }

    private static NewBank getBankInstance() {
        // Try common singleton accessors first
        String[] candidates = {"getBank", "getInstance", "getNewBank"};

        for (String name : candidates) {
            try {
                Method m = NewBank.class.getDeclaredMethod(name);
                m.setAccessible(true);
                Object obj = m.invoke(null);
                if (obj instanceof NewBank) {
                    return (NewBank) obj;
                }
            } catch (Exception ignored) {}
        }

        // Last resort: reflectively call the private constructor
        try {
            Constructor<NewBank> c = NewBank.class.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not obtain NewBank instance. Check singleton accessor.", e);
        }
    }
}
