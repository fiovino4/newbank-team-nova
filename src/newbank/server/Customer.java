package newbank.server;

import java.util.ArrayList;

public class Customer {

	private final String password;
	private final ArrayList<Account> accounts;

	public Customer(String password) {
        this.password = password;
        accounts = new ArrayList<>();
    }
	

	public boolean checkPassword(String pw) {
        return password.equals(pw);
    }

    public String accountsToString() {
        StringBuilder sb = new StringBuilder();
        for (Account a : accounts) {
            if (!sb.isEmpty()) {
                sb.append("\n");
            }
            sb.append("> ").append(a.toString());
        }
        return sb.toString();
    }


	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
