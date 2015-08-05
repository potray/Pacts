package es.potrayarrick.pacts.backend.Utils;

/**
 * Just a string wrapper for messaging.
 */
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
}
