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

        this.accounts = new ArrayList<>();
    }


	public boolean checkPassword(String passwordToCheck){

		try {
			//use the new verify method
			return PasswordManagerService.verify(passwordToCheck, passwordHash, passwordSalt);

		} catch (Exception e) {

            throw new RuntimeException("Password verification failed", e);
        }
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
