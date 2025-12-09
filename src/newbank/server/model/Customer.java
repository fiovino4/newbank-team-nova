package newbank.server.model;

import newbank.server.service.security.PasswordManagerService;

public class Customer {

    private final String passwordHash;
    private final String passwordSalt;

    public Customer(String userInputPassword) {
        try {
            PasswordManagerService.HashResult result =
                    PasswordManagerService.hashPassword(userInputPassword);

            this.passwordHash = result.hash;
            this.passwordSalt = result.salt;

        } catch (Exception e) {
            throw new RuntimeException("Error hashing password process", e);
        }
    }

    public boolean checkPassword(String passwordToCheck) {
        try {
            return PasswordManagerService.verify(passwordToCheck, passwordHash, passwordSalt);
        } catch (Exception e) {
            throw new RuntimeException("Password verification failed", e);
        }
    }
}
