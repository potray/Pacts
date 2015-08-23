package es.potrayarrick.pacts.backend.apis;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.inject.Named;

import es.potrayarrick.pacts.backend.Utils.Message;
import es.potrayarrick.pacts.backend.Utils.PasswordHash;
import es.potrayarrick.pacts.backend.models.User;

import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * The API for user registration.
 */

@Api(name = "register",
        version = "0.1",
        namespace = @ApiNamespace(
                ownerDomain = "es.potrayarrick.pacts.backend",
                ownerName = "es.potrayarrick.pacts.backend",
                packagePath = ""
        ))

public class Register {
    /**
     * Register a new user.
     *
     * @param email    the user email.
     * @param password the user password.
     * @param name     the user name.
     * @param surname  the user surname.
     * @return a message telling success or error.
     */
    @ApiMethod(name = "userRegistration")
    public final Message userRegistration(@Named("email") final String email,
                                          @Named("password") final String password,
                                          @Named("name") final String name,
                                          @Named("surname") final String surname) {
        //Check if email exists
        User user = ofy().load().type(User.class).id(email).now();
        if (user != null) {
            //User exists, return error
            return new Message(Message.ERROR);
        } else {
            //Create user and return success
            User newUser;
            try {
                newUser = new User(email, PasswordHash.createHash(password));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new Message(Message.ERROR);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return new Message(Message.ERROR);
            }
            newUser.setName(name);
            newUser.setSurname(surname);

            ofy().save().entity(newUser);
            return new Message(Message.SUCCESS);
        }
    }
}
