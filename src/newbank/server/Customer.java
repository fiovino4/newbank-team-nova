package newbank.server;

import newbank.server.security.PasswordManagerService;
import java.util.ArrayList;

public class Customer {

	private final ArrayList<Account> accounts;
	private final String passwordHash;
	private final String passwordSalt;

	public Customer(String userInputPassword) {

		try {
			PasswordManagerService.HashResult result = PasswordManagerService.hashPassword(userInputPassword);

			this.passwordHash = result.hash;
			this.passwordSalt = result.salt;

			//System.out.println("this is the pass stored" + passwordHash + " " + passwordSalt);

		} catch (Exception e) {
            throw new RuntimeException("Error hashing password process", e);
        }

        accounts = new ArrayList<>();
    }


	public boolean checkPassword(String passwordToCheck){

		try {
			//use the new verify method
			return PasswordManagerService.verify(passwordToCheck, passwordHash, passwordSalt);

		} catch (Exception e) {

            throw new RuntimeException(e);
        }
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
