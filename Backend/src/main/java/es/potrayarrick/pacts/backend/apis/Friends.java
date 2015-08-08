package es.potrayarrick.pacts.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;

import java.util.ArrayList;

import javax.inject.Named;

import es.potrayarrick.pacts.backend.Utils.Message;
import es.potrayarrick.pacts.backend.models.FriendRequest;
import es.potrayarrick.pacts.backend.models.User;

import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * A class for friends management.
 */

@Api(name = "friends",
        version = "0.1",
        namespace = @ApiNamespace(
                ownerDomain = "es.potrayarrick.pacts.backend",
                ownerName = "es.potrayarrick.pacts.backend",
                packagePath = ""
        ))
public class Friends {
    /**
     * String for accepting a friend request.
     */
    public static final String ACCEPT = "accept friend request";
    /**
     * String for rejecting a friend request.
     */
    public static final String REJECT = "reject friend request";
    /**
     * A user sends a friend request to another.
     * @param senderEmail the user who sent the request.
     * @param receiverEmail the user to receive the request.
     * @return success if the request was sent, error if not.
     */
    @ApiMethod(name = "sendFriendRequest")
    public final Message sendFriendRequest(@Named ("senderEmail") final String senderEmail,
                                            @Named ("receiverEmail") final String receiverEmail) {
        User sender = ofy().load().type(User.class).id(senderEmail).now();
        User receiver = ofy().load().type(User.class).id(receiverEmail).now();

        //Check if receiver exists.
        if (receiver == null){
            return new Message(Message.ERROR);
        } else {
            Key<User> senderKey = Key.create(sender);
            Key<User> receiverKey = Key.create(receiver);

            //Create the request
            FriendRequest request = new FriendRequest(senderKey, receiverKey);
            ofy().save().entity(request).now();
            Key<FriendRequest> requestKey = Key.create(request);
            sender.sendFriendRequest(requestKey);
            receiver.receiveFriendRequest(requestKey);

            //Save entities
            ofy().save().entity(sender);
            ofy().save().entity(receiver);

            return new Message(Message.SUCCESS);
        }
    }


    /**
     * Answer a friend request.
     * @param requestKey The request of the request to answer.
     * @param answer The answer.
     * @return A message with SUCCESS, for communication purposes.
     */
    @ApiMethod(name = "answerFriendRequest")
    public final Message answerFriendRequest(@Named ("requestKey") final String requestKey,
                                             @Named ("answer") final String answer) {
        //Get the request
        FriendRequest request = (FriendRequest) ofy().load().key(Key.create(requestKey)).now();

        switch (answer) {
            case FriendRequest.ACCEPT_ANSWER:
                //Add friends and delete the request.
                Key<User> receiverKey, senderKey;
                receiverKey = request.getReceiver();
                senderKey = request.getSender();

                User receiver = ofy().load().key(receiverKey).now();
                User sender = ofy().load().key(senderKey).now();

                receiver.addFriend(senderKey);
                sender.addFriend(receiverKey);

                ofy().save().entity(receiver);
                ofy().save().entity(sender);
                //TODO send a notification message to the sender.
                break;
            case FriendRequest.REFUSE_ANSWER:
                //TODO send a notification message to the sender.
            break;
            default:
                break;

        }
        //The request needs to be deleted.
        ofy().delete().entity(request);

        return new Message(Message.SUCCESS);
    }

    /**
     * Find user by email.
     * @param email the email of the user to find.
     * @return the user if found, null if not.
     */

    @ApiMethod(name = "getUserFriends")
    public final ArrayList<User> getUserFriends(@Named("email") final String email) {
        //Find user
        User user = ofy().load().type(User.class).id(email).now();
        return user.getFriends();
    }
}
