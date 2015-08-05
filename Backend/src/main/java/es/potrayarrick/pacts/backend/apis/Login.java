package es.potrayarrick.pacts.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

import es.potrayarrick.pacts.backend.Utils.Encryption;
import es.potrayarrick.pacts.backend.models.User;

import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * A class for user login.
 */

@Api(name = "login",
        version = "0.1",
        namespace = @ApiNamespace(
                ownerDomain = "es.potrayarrick.pacts.backend",
                ownerName = "es.potrayarrick.pacts.backend",
                packagePath = ""
        ))
public class Login {

    /**
     * Login a user.
     * @param email the email of the user.
     * @param password the password of the user.
     * @return null if the user exists, the new user if it doesn't.
     */
    @ApiMethod(name = "userLogin")
    public final User userLogin(@Named("email") final String email,
                                @Named("password") final String password) {

        //Check if email exists.
        User user = ofy().load().type(User.class).id(email).now();
        if (user != null) {
            //User exists, check password.
            if (user.getPassword().equals(Encryption.sha256Encrypt(password))) {
                return user;
            } else {
                //Incorrect password.
                return null;
            }
        } else {
            //User doesn't exists.
            return null;
        }
    }

    /**
     * This is just a test method to check persistence. It shouldn't be used.
     * @param email an email to check.
     * @return a string containing the email and password of the user.
     */
    private User testMethod(final String email) {
        User u = ofy().load().type(User.class).filter("email", email).first().now();
        return u;
    }
}
