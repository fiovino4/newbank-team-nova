package newbank.tests;

import org.junit.jupiter.api.Test;

import newbank.server.Account;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    public void shouldProcessDepositTransaction() {
        Account account = new Account("Deposit", 100.0);
        Transaction deposit = new Transaction(account, 50.0, Transaction.Type.DEPOSIT);
        deposit.process();
        assertEquals(150.0, account.getBalance());
    }
    // Verifies that a deposit transaction increases the account balance.

    @Test
    public void shouldProcessWithdrawalTransaction() {
        Account account = new Account("Withdraw", 100.0);
        Transaction withdrawal = new Transaction(account, 40.0, Transaction.Type.WITHDRAW);
        withdrawal.process();
        assertEquals(60.0, account.getBalance());
    }
    // Checks that a withdrawal transaction decreases the account balance.
}