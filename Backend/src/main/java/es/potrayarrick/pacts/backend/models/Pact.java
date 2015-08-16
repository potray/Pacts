package es.potrayarrick.pacts.backend.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;


import static es.potrayarrick.pacts.backend.OfyService.ofy;


/**
 * The pact model.
 */
@Entity
public class Pact {
    /**
     * The pact id.
     */
    @Id
    private Long id;

    /**
     * The name of the pact.
     */
    private String name;

    /**
     * The description of the pact.
     */
    private String description;

    /**
     * If this pact is in fact a promise instead of a pact.
     */
    private boolean isPromise;

    /**
     * The type of the pact. Null if {@link #isPromise} = true.
     * @see PactType
     */
    private PactType type;

    /**
     * The creation date of the pact.
     */
    @Index
    private Date creationDate;

    /**
     * Whether the pact if fulfilled or not.
     */
    private boolean isFulfilled;

    /**
     * The fulfillment date.
     */
    @Index
    private Date fulfillDate;

    /**
     * Whether this pact is broken or not.
     */
    private boolean isBroken;

    /**
     * The date the pact was broken.
     */
    @Index
    private Date brokenDate;

    /**
     * The users of the pact.
     */
    private Key<User> user1, user2;

    /**
     * No-arg constructor for objectify.
     */
    @SuppressWarnings("unused")
    public Pact() { }



    /**
     * Default constructor.
     * @param name the pact name.
     * @param description the pact description.
     * @param user1 the first user of the pact.
     * @param user2 the second user of the pact.
     */
    public Pact(final String name, final String description, final User user1, final User user2) {
        this.name = name;
        this.description = description;
        isFulfilled = false;
        isBroken = false;
        creationDate = new Date();
        this.user1 = Key.create(user1);
        this.user2 = Key.create(user2);
    }

    /**
     * Sets the description.
     * @param description the new description.
     */
    public final void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Sets the is promise.
     * @param isPromise the new value.
     */
    public final void setPromise(final boolean isPromise) {
        this.isPromise = isPromise;
        if (isPromise) {
            // Promises don't have a pact type.
            this.type = null;
        }
    }

    /**
     * Sets the pact type.
     * @param type the new type.
     */
    public final void setType(final PactType type) {
        this.type = type;
        // Pacts with types are not promises.
        isPromise = false;
    }

    /**
     * Breaks the pact.
     */
    public final void breakPact() {
        isBroken = true;
        brokenDate = new Date();
    }

    /**
     * Fulfills the pact.
     */
    public final void fullfill() {
        isFulfilled = true;
        fulfillDate = new Date();
    }

    /**
     * Gets the email of the user 1.
     * @return the email of the user1.
     */
    @SuppressWarnings("unused") // This is for the client.
    public final String getUser1Email() {
        return ofy().load().key(user1).now().getEmail();
    }

    /**
     * Gets the complete name of the user 1.
     * @return the name and the surname of the user1.
     */
    @SuppressWarnings("unused") // This is for the client.
    public final String getUser1CompleteName() {
        User user = ofy().load().key(user1).now();
        return (user.getName() + " " + user.getSurname());
    }

    /**
     * Gets the complete name of the user 2.
     * @return the name and the surname of the user1.
     */
    @SuppressWarnings("unused") // This is for the client.
    public final String getUser2CompleteName() {
        User user = ofy().load().key(user2).now();
        return (user.getName() + " " + user.getSurname());
    }

    public final String getName() {
        return this.name;
    }
}
