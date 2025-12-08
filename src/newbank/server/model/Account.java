package newbank.server.model;

public class Account {
	
	private String accountName;
	//private double openingBalance;
	 private double balance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.balance = openingBalance;
	}

	@Override
	public String toString() {
		return (accountName + ": " + balance);
	}
	 public String getAccountName() {
        return accountName;
    }


	public void setBalance(double balance) {
        this.balance = balance;
    }
	

	public String getAccountByName() {

		return  accountName;
	}

	public double getBalance(){

		return balance;
	}
}
