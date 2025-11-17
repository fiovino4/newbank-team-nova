package newbank.server;

import java.util.ArrayList;

public class Customer {

	private String password;
	private ArrayList<Account> accounts;

	public Customer(String password) {
        this.password = password;
        accounts = new ArrayList<>();
    }
	

	public boolean checkPassword(String pw) {
        return password.equals(pw);
    }

	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
