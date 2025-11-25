package newbank.server.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class responsible for securely hashing and verifying passwords
 * using the PBKDF2WithHmacSHA256 algorithm.
 *
 * <p>This class provides:
 * <ul>
 *     <li>Generation of a salted password hash</li>
 *     <li>Verification of a user-provided password against a stored hash</li>
 * </ul>
 *
 *
 * N.B Never store raw passwords. Store only the hash and salt
 * produced by the {@link #hashPassword(String)} method.
 */
public class PasswordManagerService {

    /**
     * Represents the result of a password hashing operation.
     * <p>Contains both:
     * <ul>
     *     <li>The Base64-encoded hash</li>
     *     <li>The Base64-encoded salt</li>
     * </ul>
     */
    public static class HashResult {

        /** Base64-encoded PBKDF2 hash of the password. */
        public final String hash;

        /** Base64-encoded salt used during hashing. */
        public final String salt;

        /**
         * Constructor method to generate a new HashResult.
         *
         * @param hash  the Base64-encoded password hash
         * @param salt  the Base64-encoded salt value
         */
        public  HashResult (String hash, String salt){
            this.hash = hash;
            this.salt = salt;
        }
    }


    /**
     * Generates a secure, salted PBKDF2 hash from the given raw password.
     *
     * <p>The method performs the following steps:
     * <ol>
     *     <li>Generates a random 16-byte salt</li>
     *     <li>Hashes the password using PBKDF2WithHmacSHA256</li>
     *     <li>Returns the Base64-encoded hash and salt</li>
     * </ol>
     *
     * @param password the raw password to hash
     * @return a {@link HashResult} containing the hash and salt
     * @throws Exception if the hashing algorithm is not available or a cryptographic error occurs
     */

    public  static  HashResult hashPassword(String password) throws  Exception {

        int iteration = 65536;
        int keyLength = 256;

        byte[] salt = new byte[16];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        PBEKeySpec spc = new PBEKeySpec(password.toCharArray(), salt, iteration, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashBytes = skf.generateSecret(spc).getEncoded();

        HashResult hashResult = new HashResult(Base64.getEncoder().encodeToString(hashBytes), Base64.getEncoder().encodeToString(salt));

        return  hashResult;
    }


    /**
     * Verifies whether a user-provided password matches a previously stored hash.
     *
     * <p>The method:
     * <ol>
     *     <li>Decodes the stored Base64 salt</li>
     *     <li>Recomputes the PBKDF2 hash using the same parameters</li>
     *     <li>Compares the new hash to the stored hash</li>
     * </ol>
     *
     * @param password   the password provided by the user
     * @param storedHash the stored Base64-encoded hash
     * @param storedSalt the stored Base64-encoded salt
     * @return {@code true} if the password matches, {@code false} otherwise
     * @throws Exception if a cryptographic error occurs
     */
    public static boolean verify(String password, String storedHash, String storedSalt) throws Exception{

        int iteration = 65536;
        int keyLength = 256;

        byte[] salt = Base64.getDecoder().decode(storedSalt);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iteration, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] newHash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(newHash).equals(storedHash);
    }
}
