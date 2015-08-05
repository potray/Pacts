package es.potrayarrick.pacts.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class User {
    /**
     * The email of the user.
     */
    @Id
    private String email;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * No-arg constructor for objectify.
     */
    public User() { }

    /**
     * Basic constructor.
     * @param newUserEmail user email.
     * @param newUserPassword user password.
     */
    public User(final String newUserEmail, final String newUserPassword) {
        this.email = newUserEmail;
        this.password = newUserPassword;
    }

    /**
     * Get user email.
     * @return the email of the user.
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Get user password.
     * @return the password of the user.
     */
    public final String getPassword() {
        return password;
    }
}
