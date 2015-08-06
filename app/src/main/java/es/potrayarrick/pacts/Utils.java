package es.potrayarrick.pacts;

/**
 * A class with several utilities.
 */
public final class Utils {
    //Constants
    /**
     * The minimum size of a password.
     */
    private static final int MIN_PASSWORD_SIZE = 8;

    /**
     * If I'm testing on my local machine or in GAE.
     */
    public static final boolean LOCAL_TESTING = false;

    /**
     * File name for shared preferences.
     */
    public static final String PREFS_NAME = "PactsPrefsFile";

    /**
     * Private constructor to prevent instances.
     */
    private Utils() { }

    /**
     * Validates the format of an email.
     * @param email the email to validate.
     * @return true if it's a valid email, false if not.
     */
    public static boolean isEmailValid(final String email) {
        String emailRegex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);

    }

    /**
     * Validates the format of a password.
     * @param password the password to validate.
     * @return true if the password is 8 or more characters long, false if not.
     */
    public static boolean isPasswordValid(final String password) {
        return password.length() >= MIN_PASSWORD_SIZE;
    }

    /**
     * A class for frequently used strings.
     */
    public final class Strings {
        /**
         * The email of a user.
         */
        public static final String USER_EMAIL = "user email";

        // Of course we don't want to store user passwords on the device, so no string needed
        // for that.

        /**
         * The name of a user.
         */
        public static final String USER_NAME = "user name";

        /**
         * The surname of a user.
         */
        public static final String USER_SURNAME = "user surname";


        /**
         * Private constructor to prevent instances.
         */
        private Strings() { }
    }
}
