package newbank.server;

import newbank.server.model.Account;
import newbank.server.model.Customer;
import newbank.server.model.CustomerID;
import newbank.server.model.Loan;
import newbank.server.service.NotificationService;
import newbank.server.service.LoanService;

import java.util.HashMap;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private final HashMap<String, Customer> customers;

	private final LoanService loanService;
	private final NotificationService notificationService;

	private NewBank() {
		customers = new HashMap<>();
		loanService = new LoanService(this);

		addTestLoans();

		notificationService = new NotificationService();

		addTestData();
	}


	//getter of loan Service
	public LoanService getLoanService() {
		return loanService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	private void addTestData() {
		Customer bhagy = new Customer("1234");
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);

		//mock a loan for Bhagy ----
		CustomerID bhagyId  = new CustomerID("Bhagy");
		Loan mockLoan = loanService.offerLoan(bhagyId, "Main", 500, 5.0, 12, "Mock loan for testing");

		Customer christina = new Customer("abcd");
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);

		Customer john = new Customer("pass");
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);

		Customer test = new Customer("Test");
		test.addAccount(new Account("Main", 1000.0));
		test.addAccount(new Account("Savings", 1000.0));
		test.addAccount(new Account("Bonds", 1000.0));
		customers.put("Test", test);
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


	public static NewBank getBank() {
		return bank;
	}

	public Customer getCustomer(String userName) {
		return customers.get(userName);
	}

	public boolean hasCustomer(CustomerID customerID) {
		return customers.containsKey(customerID.getKey());
	}

	public String showMyAccounts(CustomerID customerID) {
		Customer c = customers.get(customerID.getKey());
		if (c == null) {
			return "FAIL: Unknown customer.";
		}
		return c.accountsToString();
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		Customer customer = customers.get(userName);
		if (customer != null && customer.checkPassword(password)) {
			return new CustomerID(userName);
		}
		return null;
	}
}
