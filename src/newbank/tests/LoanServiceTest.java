package newbank.tests;


import newbank.server.NewBank;
import newbank.server.model.CustomerID;
import newbank.server.service.LoanService;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    @Test
    public void negativeLoanNotAddedToMarketplace() {
        NewBank bank = NewBank.getBank();
        LoanService loanService = bank.getLoanService();

        CustomerID lender = new CustomerID("Test"); // exists in test data
        String fromAccount = "Main";

        String before = loanService.showAvailableLoans();

        assertThrows(IllegalArgumentException.class, () ->
                loanService.offerLoan(lender, fromAccount, -100.0, 5.0, 12, "Negative loan")
        );

        String after = loanService.showAvailableLoans();
        assertEquals(before, after, "Marketplace should be unchanged after invalid loan attempt");
    }
}
// Verifies that offering a negative loan amount is rejected and does not alter the loan marketplace.
