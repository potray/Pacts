package es.potrayarrick.pacts.backend.Utils;

import com.googlecode.objectify.annotation.Entity;

/**
 * Just a string wrapper for messaging.
 */
@Entity
public class Message {
    /**
     * The message.
     */
    private final String str;

    //Static strings.
    /**
     * Error string.
     */
    public static final String ERROR = "Error";
    /**
     * Success string.
     */
    public static final String SUCCESS = "Success";

    /**
     * Already friends string.
     */
    public static final String ERROR_ALREADY_FRIENDS = "Already friends";

    /**
     * User not found string.
     */
    public static final String ERROR_USER_NOT_FOUND = "User not found";

    /**
     * Default constructor.
     * @param str the message.
     */
    public Message(final String str) {
        this.str = str;
    }

    /**
     * Get the message.
     * @return the message.
     */
    public final String getStr() {
        return str;
    }

    /**
     * Method for check if the message is a success message in case the constants are inaccessible
     * (Android client).
     * @return if the message is success.
     */
    public final boolean isSuccess() {
        return str.equals(SUCCESS);
    }

    /**
     * Method for check if the message is an error message in case the constants are inaccessible
     * (Android client).
     * @return if the message is success.
     */
    public final boolean isError() {
        return str.equals(ERROR);
    }
}
