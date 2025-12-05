package newbank.server;

public class Loan {

    private static int nextId = 1;

    private final int id;
    private final String lenderCustomerName;
    private final String fromAccountName;
    private final double amount;
    private final double annualRate;   // e.g. 5.0 means 5% per year
    private final int termMonths;
    private final String extraTerms;

    private String status;             // e.g. "OPEN", "TAKEN", "CANCELLED"

    public Loan(String lenderCustomerName,
                String fromAccountName,
                double amount,
                double annualRate,
                int termMonths,
                String extraTerms) {

        this.id = nextId++;
        this.lenderCustomerName = lenderCustomerName;
        this.fromAccountName = fromAccountName;
        this.amount = amount;
        this.annualRate = annualRate;
        this.termMonths = termMonths;
        this.extraTerms = extraTerms == null ? "" : extraTerms;
        this.status = "OPEN";
    }

    public int getId() {
        return id;
    }

    public String getLenderCustomerName() {
        return lenderCustomerName;
    }

    public String getFromAccountName() {
        return fromAccountName;
    }

    public double getAmount() {
        return amount;
    }

    public double getAnnualRate() {
        return annualRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public String getExtraTerms() {
        return extraTerms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(id)
          .append(" | Lender: ").append(lenderCustomerName)
          .append(" | From: ").append(fromAccountName)
          .append(" | Amount: ").append(amount)
          .append(" | Rate: ").append(annualRate).append("%")
          .append(" | Term: ").append(termMonths).append(" months");
        if (!extraTerms.isEmpty()) {
            sb.append(" | Terms: ").append(extraTerms);
        }
        sb.append(" | Status: ").append(status);
        return sb.toString();
    }
}
