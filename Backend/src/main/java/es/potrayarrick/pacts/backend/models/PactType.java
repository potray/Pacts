package es.potrayarrick.pacts.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * An entity for storing pact types. We don't want thousands of "blood" types, so it will be unique.
 */
@Entity
public class PactType {

    /**
     * The type.
     */
    @Id
    private String type;

    /**
     * No-arg constructor for objectify.
     */
    @SuppressWarnings("unused")
    public PactType() { }

    /**
     * Default constructor.
     * @param type the type.
     */
    public PactType(final String type) {
        // We don't want blood, Blood, BLOOD, BlOoD and bLoOd in the database.
        this.type = type.toLowerCase();
    }


    public String getType() {
        return type;
    }
}
