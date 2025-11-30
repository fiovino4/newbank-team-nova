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
            if (sb.length() > 0) {
            /*if (!sb.isEmpty()) {*/ /*removed this part */
                sb.append("\n");
            }
            sb.append("> ").append(a.toString());
        }
        return sb.toString();
    }


	public void addAccount(Account account) {
		accounts.add(account);		
	}

public boolean hasAccount(String accountName) {
    for (Account a : accounts) {
        if (a.getAccountName().equalsIgnoreCase(accountName)) {
            return true;
        }
    }
    return false;
}

public Account getAccount(String accountName) {
    for (Account a : accounts) {
        if (a.getAccountName().equalsIgnoreCase(accountName)) {
            return a;
        }
    }
    return null;
}

public Account getAccount(String accountName) {
    for (Account a : accounts) {
        if (a.getAccountName().equalsIgnoreCase(accountName)) {
            return a;
        }
    }
    return null;
}

public boolean removeAccount(String accountName) {
    for (int i = 0; i < accounts.size(); i++) {
        Account a = accounts.get(i);
        if (a.getAccountName().equalsIgnoreCase(accountName)) {
            accounts.remove(i);
            return true;
        }
    }
    return false;
}


}
