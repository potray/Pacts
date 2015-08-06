package es.potrayarrick.pacts.backend;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import es.potrayarrick.pacts.backend.Utils.PasswordHash;
import es.potrayarrick.pacts.backend.models.FriendRequest;
import es.potrayarrick.pacts.backend.models.User;

/**
 * A wrapper class for the Objectify Service.
 */
public final class OfyService {
    /**
     * For debugging.
     */
    private static final boolean DEBUG = true;

    /**
     * Private constructor so this class can't be instanced.
     */
    private OfyService() { }

    static {
        factory().register(User.class);
        factory().register(FriendRequest.class);
        //If I'm debugging populate the database with testing entities. Since this only executes
        //once I do it here.
        if (DEBUG) {
            User testUser, testFriend;
            FriendRequest request;
            try {
                System.out.println("Debugging");
                testUser = new User("test@test.com", PasswordHash.createHash("qqqqqqqq"));
                testUser.setName("Test");
                testUser.setSurname("Testing");

                testFriend = new User("friend@friend.com", PasswordHash.createHash("qqqqqqqq"));
                testFriend.setName("Friend");
                testFriend.setSurname("Friendly");

                ofy().save().entity(testUser);
                ofy().save().entity(testFriend);

                Key<User> testUserKey = Key.create(testUser);
                Key<User> testFriendKey = Key.create(testFriend);

                request = new FriendRequest(testUserKey, testFriendKey);

                ofy().save().entity(testUser);
                ofy().save().entity(testFriend);
                ofy().save().entity(request);


                System.out.println("Debugged!");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A time saver. Just for typing less code.
     * @return the Objectify service.
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    /**
     * Another time server for typing less code.
     * @return the Objectify factory.
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
