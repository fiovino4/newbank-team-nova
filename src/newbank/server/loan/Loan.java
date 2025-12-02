package newbank.server.loan;

import newbank.server.CustomerID;

/**
 * Represents a loan offered within the NewBank system.
 *
 * A Loan is created when a customer offers to lend money from one of their
 * accounts. The class stores all financial details of the offer: the principal
 * amount, interest rate, repayment term, the originating account, and optional
 * additional terms. It also tracks the current status of the loan as it moves
 * through the loan lifecycle.
 *
 * Responsibilities of this class:
 * - Store identifying information such as the loan ID and the lender.
 * - Store the loan's financial parameters.
 * - Store and allow updating of the loan's status.
 *
 * All validation before creating a loan is handled by LoanService.
 * This class acts strictly as a domain model.
 */
public class Loan {

    private final long id;
    private final CustomerID lender;
    private final String fromAccount;
    private final double amount;
    private final double interestRate;
    private final int termMonths;
    private final String extraTerms;
    private LoanStatus loanStatus;


    public Loan (int id, CustomerID lender, String fromAccount, double amount, double interestRate, int termMonths, String extraTerms, LoanStatus loanStatus){

        this.id = id;
        this.lender = lender;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.extraTerms = extraTerms;
        this.loanStatus = loanStatus;

    }


    public long getId() {
        return id;
    }

    public CustomerID getLender() {
        return lender;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public String getExtraTerms() {
        return extraTerms;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }


    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("Load ID: ").append(id).
                append(", Lender").append(lender.getKey()).
                append(", From account: ").append(fromAccount).
                append(", Amount of loan: ").append(amount).
                append(", Term ").append(termMonths).append(" % per year").
                append(", Status").append(loanStatus);

        if(extraTerms == null || extraTerms.isEmpty()){
            sb.append(" ");
        }else {
            sb.append(", with extra Terms: ").append(extraTerms);
        }

        return  sb.toString();
    }
}
