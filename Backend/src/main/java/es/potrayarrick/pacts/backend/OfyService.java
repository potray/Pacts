package es.potrayarrick.pacts.backend;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import es.potrayarrick.pacts.backend.Utils.PasswordHash;
import es.potrayarrick.pacts.backend.models.FriendRequest;
import es.potrayarrick.pacts.backend.models.Pact;
import es.potrayarrick.pacts.backend.models.PactRequest;
import es.potrayarrick.pacts.backend.models.PactType;
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
        factory().register(Pact.class);
        factory().register(PactType.class);
        factory().register(PactRequest.class);
        //If I'm debugging populate the database with testing entities. Since this only executes
        //once I do it here.
        if (DEBUG) {
            User testUser, testFriend, testFriend2;
            FriendRequest request, request2;
            try {
                System.out.println("Debugging");
                testUser = new User("test@test.com", PasswordHash.createHash("qqqqqqqq"));
                testUser.setName("Test");
                testUser.setSurname("Testing");

                testFriend = new User("friend@friend.com", PasswordHash.createHash("qqqqqqqq"));
                testFriend.setName("Friend");
                testFriend.setSurname("Friendly");

                testFriend2 = new User("friend2@friend.com", PasswordHash.createHash("qqqqqqqq"));
                testFriend2.setName("Friend2");
                testFriend2.setSurname("Friendly2");

                ofy().save().entity(testUser);
                ofy().save().entity(testFriend);
                ofy().save().entity(testFriend2);

                Key<User> testUserKey = Key.create(testUser);
                Key<User> testFriendKey = Key.create(testFriend);
                Key<User> testFriend2Key = Key.create(testFriend2);

                testFriend2.addFriend(testUserKey);
                testUser.addFriend(testFriend2Key);

                request = new FriendRequest(testUserKey, testFriendKey);
                request2 =  new FriendRequest(testFriendKey, testUserKey);

                ofy().save().entity(request).now();
                ofy().save().entity(request2).now();

                Key<FriendRequest> requestKey = Key.create(request);
                Key<FriendRequest> request2Key = Key.create(request2);

                testUser.sendFriendRequest(requestKey);
                testFriend.receiveFriendRequest(requestKey);
                testFriend.sendFriendRequest(request2Key);
                testUser.receiveFriendRequest(request2Key);


                Pact testPact = new Pact("Test pact", "A pact for testing", testFriend2, testUser);
                ofy().save().entity(testPact).now();

                PactRequest testRequest = new PactRequest(testFriend2Key, testUserKey, testPact);
                ofy().save().entity(testRequest).now();

                testUser.receivePactRequest(testRequest);
                testFriend2.sendPactRequest(testRequest);



                ofy().save().entity(testUser).now();
                ofy().save().entity(testFriend).now();
                ofy().save().entity(testFriend2).now();


                System.out.println("Debugged!");
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
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
