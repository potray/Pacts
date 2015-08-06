package es.potrayarrick.pacts.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The user of the application.
 */
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
     * The name of the user.
     */
    private String name;

    /**
     * The surname of the user.
     */
    private String surname;

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

    /**
     * Get user surname.
     * @return the surname of the user.
     */
    public final String getSurname() {
        return surname;
    }

    /**
     * Set user surname.
     * @param surname the new surname of the user.
     */
    public final void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * Get user name.
     * @return the name of the user.
     */
    public final String getName() {
        return name;
    }

    /**
     * Set user name.
     * @param name the new name of the user.
     */
    public final void setName(final String name) {
        this.name = name;
    }


    /**
     * Get the user key.
     * @return the key of the user.
     */
    /*@Transient
    public final Key<User> getKey() {
        return Key.create(User.class, email);
    }*/

}
