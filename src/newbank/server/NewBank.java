package newbank.server;

import newbank.server.loan.LoanService;

import java.util.HashMap;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private final HashMap<String, Customer> customers;

	private final LoanService loanService;

	private NewBank() {
		customers = new HashMap<>();
		addTestData();
		loanService = new LoanService(this);
	}


	//getter of loan Service
	public LoanService getLoanService() {
		return loanService;
	}

	private void addTestData() {
		Customer bhagy = new Customer("1234");
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);

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
