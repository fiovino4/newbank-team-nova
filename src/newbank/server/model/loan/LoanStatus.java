package newbank.server.model.loan;

/**
 * Represents the possible lifecycle states of a Loan.
 *
 * AVAILABLE: The loan has been offered and is waiting for a borrower.
 *
 * REQUESTED: A borrower has requested to accept the loan, pending approval.
 *
 * ACTIVE: The loan has been accepted and is currently active and in repayment.
 *
 * REPAID: The loan has been fully repaid.
 *
 * CANCELLED: The loan offer has been withdrawn or cancelled.
 */
public enum LoanStatus {
    AVAILABLE,
    REQUESTED,
    ACTIVE,
    REPAID,
    CANCELLED
}
