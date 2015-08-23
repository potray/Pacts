package es.potrayarrick.pacts.backend.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * The friend request model.
 */
@Entity
public class FriendRequest {
    /**
     * The ID of the request.
     */
    @Id
    private Long id;
    /**
     * The user who sent the request.
     */
    private Key<User> sender;
    /**
     * The user the sender sent the request to.
     */
    private Key<User> receiver;

    /**
     * Accept a friend request string.
     */
    public static final String ACCEPT_ANSWER = "accept";
    /**
     * Refuse a friend request string.
     */
    public static final String REFUSE_ANSWER = "refuse";

    /**
     * No-arg constructor for objectify.
     */
    @SuppressWarnings("unused")
    public FriendRequest() {
    }

    /**
     * Default constructor.
     *
     * @param sender   The key of the user who sent the request.
     * @param receiver The -key of the user the sender sent the request to.
     */
    public FriendRequest(final Key<User> sender, final Key<User> receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Get the user who sent the request.
     *
     * @return the user who sent the request.
     */
    public final User getSender() {
        return ofy().load().key(sender).now();
    }

    /**
     * Get the user who received the request.
     *
     * @return the user who received the request.
     */
    public final User getReceiver() {
        return ofy().load().key(receiver).now();
    }

    /**
     * Get the request id.
     *
     * @return the request id.
     */
    @SuppressWarnings("unused") // The frontend uses this method.
    public final Long getId() {
        return id;
    }

}
