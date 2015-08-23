package es.potrayarrick.pacts.backend.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;


import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * The pact request model.
 */
@Entity
public class PactRequest extends FriendRequest {
    /**
     * The pact of the request.
     */
    @Index
    private Key<Pact> pact;

    /**
     * Empty constructor for Objectify.
     */
    @SuppressWarnings("unused")
    public PactRequest() {
    }

    /**
     * Default constructor.
     *
     * @param sender   request sender.
     * @param receiver request receiver.
     * @param pact     the pact.
     */
    public PactRequest(final Key<User> sender, final Key<User> receiver, final Pact pact) {
        super(sender, receiver);
        this.pact = Key.create(pact);
    }

    /**
     * Gets {@link #pact}.
     *
     * @return {@link #pact}.
     */
    public final Pact getPact() {
        return ofy().load().key(pact).now();
    }
}
