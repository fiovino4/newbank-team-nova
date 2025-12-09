package newbank.server;

import newbank.server.model.Account;
import newbank.server.model.Customer;
import newbank.server.model.CustomerID;
import newbank.server.model.Loan;
import newbank.server.service.AccountService;
import newbank.server.service.CustomerService;
import newbank.server.service.NotificationService;
import newbank.server.service.LoanService;

public class NewBank {

	private static final NewBank bank = new NewBank();

	private final CustomerService customerService;
	private final AccountService accountService;
	private final LoanService loanService;
	private final NotificationService notificationService;

	private NewBank() {
		this.customerService = new CustomerService();
		this.accountService = new AccountService(customerService);
		this.loanService = new LoanService(this);
		this.notificationService = new NotificationService();

		addTestData();
		addTestLoans();
	}

	// --- service getters ---

	public static NewBank getBank() {
		return bank;
	}

	public LoanService getLoanService() {
		return loanService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	// --- test data ---

	private void addTestData() {
		// Bhagy
		customerService.registerCustomer("Bhagy", "1234");
		accountService.addAccount("Bhagy", new Account("Main", 1000.0));

		// Christina
		customerService.registerCustomer("Christina", "abcd");
		accountService.addAccount("Christina", new Account("Savings", 1500.0));

		// John
		customerService.registerCustomer("John", "pass");
		accountService.addAccount("John", new Account("Checking", 250.0));

		// Test user
		customerService.registerCustomer("Test", "Test");
		accountService.addAccount("Test", new Account("Main", 1000.0));
		accountService.addAccount("Test", new Account("Savings", 1000.0));
		accountService.addAccount("Test", new Account("Bonds", 1000.0));
	}

	private void addTestLoans() {
		try {
			// Bhagy offers a loan
			loanService.offerLoan(
					new CustomerID("Bhagy"),
					"Main",
					200.0,
					5.0,
					12,
					"Test loan from Bhagy"
			);

			loanService.offerLoan(
					new CustomerID("Bhagy"),
					"Main",
					200.0,
					5.0,
					12,
					"Test loan from Bhagy"
			);

			// Christina offers a loan
			loanService.offerLoan(
					new CustomerID("Christina"),
					"Savings",
					300.0,
					4.5,
					6,
					"Short-term loan"
			);

			// Test user offers a loan
			loanService.offerLoan(
					new CustomerID("Test"),
					"Savings",
					150.0,
					3.0,
					3,
					"Demo loan"
			);
		}
		catch (IllegalArgumentException e) {
			System.out.println("Error creating test loan: " + e.getMessage());
		}
	}

	// --- customer helpers (now delegating to services) ---

	public Customer getCustomer(String userName) {
		return customerService.getCustomer(userName);
	}

	public boolean hasCustomer(CustomerID customerID) {
		return customerService.hasCustomer(customerID.getKey());
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if (customerService.authenticate(userName, password)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// Optional: if any old code still calls this
	public String showMyAccounts(CustomerID customerID) {
		return accountService.showAccounts(customerID);
	}
}
