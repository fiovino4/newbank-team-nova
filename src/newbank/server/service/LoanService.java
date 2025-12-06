package newbank.server.service;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.model.loan.Loan;
import newbank.server.model.loan.LoanStatus;
import newbank.server.notification.Notification;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service class responsible for managing loan-related operations within the NewBank system.
 *
 * This class encapsulates all business logic related to offering, tracking, and
 * maintaining loan records. It currently operates entirely in-memory using an
 * internal {@link HashMap}, and generates sequential loan identifiers through an
 * {@link AtomicInteger}. For @Version 1.0 A future implementation may persist loans to a database.
 *
 * Responsibilities include:
 *   Validating loan creation requests (amount, interest rate, term length)
 *   Ensuring the lender exists and owns the source account
 *   Preventing loans that exceed the lender's available account balance
 *   Creating new {@link Loan} instances and assigning sequential IDs
 *   Managing loan status values (initially ACTIVE for offered loans)
 *
 * The class is thread-safe for loan creation operations, as the main public entry
 * point {@link #offerLoan(CustomerID, String, double, double, int, String)}
 * is synchronized in order to avoid race condition on the seqence generated id
 *
 */
public class LoanService {

    private final NewBank newBank;

    private final Map<Integer, Loan> loans = new HashMap<>();
    private final AtomicInteger nextLoanId = new AtomicInteger(1);

    public LoanService(NewBank bank){
        this.newBank = bank;
    }


    /**
     * Creates a new loan offered by a customer and registers it within the loan store.
     *
     * This method performs business validation before creating the loan:
     *   Validates that the lender exists within the bank
     *   Ensures the specified source account belongs to the lender
     *   Ensures the loan amount is positive
     *   Prevents loans that exceed the available balance in the lender's account
     *   Validates that interest rate and term are positive
     *
     * On successful validation, a new {@link Loan} is created with a unique ID and an
     * initial status of {@link LoanStatus#AVAILABLE}. The loan is inserted into the service's
     * internal store and returned to the caller.
     *
     *
     * @param lenderId     the ID of the customer offering the loan
     * @param fromAccount  the name of the account from which the loan amount originates
     * @param amount       the amount to be loaned; must be greater than zero and not exceed the account balance
     * @param interestRate the annual interest rate (percentage); must be greater than zero
     * @param termMonths   the repayment term in months; must be greater than zero
     * @param extraTerms   optional free-text field describing additional loan terms or notes
     *
     * @return the newly created {@link Loan}
     *
     * @throws IllegalArgumentException if:
     *
     *            The lender does not exist
     *            The account does not exist for the lender
     *            The amount is zero or negative
     *            The amount exceeds the account's available balance
     *            The interest rate is zero or negative
     *            The term is zero or negative
     */
    public synchronized Loan offerLoan(CustomerID lenderId, String fromAccount, double amount, double interestRate, int termMonths, String extraTerms){

        Customer lender = newBank.getCustomer(lenderId.getKey());

        if (lender == null){
            throw new IllegalArgumentException("Unknown lender");
        }

        Account account = lender.getAccountByName(fromAccount);
        if (account == null) {
            throw new IllegalArgumentException("Account '" + fromAccount + "' does not exist.");
        }

        if(amount <= 0){
            throw new IllegalArgumentException("Amount must be grater then 0");
        }

        if(amount > account.getBalance()){
            throw  new IllegalArgumentException( "The loan amount cannot exceed the available balance of the account.");
        }

        if (interestRate <= 0){
            throw new IllegalArgumentException("Interest rate must be greater than 0.");
        }

        if (termMonths <= 0){
            throw new IllegalArgumentException("Term must be greater than 0.");
        }

        /*After the input check create the new Loan:
         - generate first the sequence for the id TODO: re-write this logic after db implementation
         - then generate the Loan object and push into the HashMap loan.
            NB: when the loan is created the status is set as ACTIVE
         */

        int id = nextLoanId.getAndIncrement();
        Loan loan = new Loan(id, lenderId, fromAccount, amount, interestRate, termMonths, extraTerms, LoanStatus.AVAILABLE);

        loans.put(id, loan);

        return loan;
    }

    /**
     * Processes a request from a borrower to request an existing loan.
     *
     * This method verifies that:
     *  - The borrower exists in the system
     *  - The loan ID refers to a valid loan
     *  - The loan is currently in AVAILABLE status
     *  - The lender referenced by the loan exists
     *
     * If all conditions are met, the loan status is changed to REQUESTED and a
     * notification is created for the lender informing them of the request.
     *
     *  @param:
     *  borrowerId - the customer who is requesting the loan
     *  loanId     - the identifier of the loan being requested
     *
     * @return :
     *  the updated Loan object with status set to REQUESTED
     *
     * @throws :
     *  IllegalArgumentException if the borrower does not exist,
     *                           if the loan does not exist,
     *                           or if the loan is not in AVAILABLE status
     *  IllegalStateException    if the loan references a lender that does not exist
     *
     * The method is synchronized to ensure thread safety when modifying loan
     * status and generating notifications.
     */
    public synchronized Loan requestLoan(CustomerID borrowerId, int loanId){

        Customer borrower = newBank.getCustomer(borrowerId.getKey());
        Loan requestedLoan = loans.get(loanId);

        if (borrower == null){
            throw new IllegalArgumentException("Unknown borrower");
        }

        if(!loans.containsKey(loanId)){
            throw new IllegalArgumentException("The loan id does not exist in the loan list");
        }

        if(requestedLoan.getLoanStatus().equals(LoanStatus.AVAILABLE)){

            requestedLoan.setLoanStatus(LoanStatus.REQUESTED);

            CustomerID lenderId = requestedLoan.getLender();
            Customer lender = newBank.getCustomer(lenderId.getKey());

            if (lender == null) {
                throw new IllegalStateException("Loan references a non-existent lender.");
            }

            Notification notification = newBank.getNotificationService().createNotification(lenderId,"Borrower " + borrowerId.getKey() +" has requested your loan " + loanId + ".");

        }else{
            throw new IllegalArgumentException("The loan is not in the AVAILABLE status");
        }

        return  requestedLoan;
    }
 }
    public synchronized String showUserLoan(CustomerID customerID) {
        if (customerID == null) {
            return "FAIL: Not logged in.";
        }

        String customerName = customerID.getKey();
        StringBuilder sb = new StringBuilder();

        for (Loan loan : loans.values()) {

            if (loan.getLender().getKey().equalsIgnoreCase(customerName)) {
                if (sb.isEmpty()) {
                    sb.append("Your loan offers:").append(System.lineSeparator());
                } else {
                    sb.append(System.lineSeparator());
                }
                sb.append(loan);
            }
        }

        if (sb.isEmpty()) {
            return "You have not created any loan offers.";
        }

        return sb.toString();
    }

    public synchronized String showAvailableLoans() {
        StringBuilder sb = new StringBuilder();

        for (Loan loan : loans.values()) {
            if (loan.getLoanStatus() == LoanStatus.AVAILABLE) {
                if (sb.isEmpty()) {
                    sb.append("Available loans:").append("\n");
                }
                sb.append(loan).append("\n");
            }
        }

        if (sb.isEmpty()) {
            return "There are currently no available loans.";
        }
        return sb.toString();
    }
}
