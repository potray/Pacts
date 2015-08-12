package es.potrayarrick.pacts.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.inject.Named;

import es.potrayarrick.pacts.backend.Utils.PasswordHash;
import es.potrayarrick.pacts.backend.models.User;

import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * The API for user login.
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
            try {
                if (PasswordHash.validatePassword(password, user.getPassword())) {
                    return user;
                } else {
                    //Incorrect password.
                    return null;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            //User doesn't exists.
            return null;
        }
    }
}
