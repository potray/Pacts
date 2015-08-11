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
        if (receiver == null) {
            return new Message(Message.ERROR_USER_NOT_FOUND);
        } else {
            Key<User> senderKey = Key.create(sender);
            Key<User> receiverKey = Key.create(receiver);

            //Check if they are friends.
            if (sender.isFriendOf(receiver)) {
                return new Message(Message.ERROR_ALREADY_FRIENDS);
            }

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
     * @param requestId The request of the request to answer.
     * @param answer The answer.
     * @return A message with SUCCESS, for communication purposes.
     */
    @ApiMethod(name = "answerFriendRequest")
    public final Message answerFriendRequest(@Named ("requestId") final long requestId,
                                             @Named ("answer") final String answer) {
        //Get the request
        FriendRequest request = ofy().load().type(FriendRequest.class).id(requestId).now();
        User receiver = request.getReceiver();
        User sender = request.getSender();

        switch (answer) {
            case FriendRequest.ACCEPT_ANSWER:
                //Add friends.
                receiver.addFriend(Key.create(sender));
                sender.addFriend(Key.create(receiver));


                //TODO send a notification message to the sender.
                break;
            case FriendRequest.REFUSE_ANSWER:
                //TODO send a notification message to the sender.
                break;
            default:
                break;

        }
        // The request needs to be deleted.
        Key<FriendRequest> requestKey = Key.create(request);
        receiver.deleteReceivedFriendRequest(requestKey);
        sender.deleteSentFriendRequest(requestKey);
        ofy().delete().entity(request);

        // Save entities
        ofy().save().entity(receiver);
        ofy().save().entity(sender);

        return new Message(Message.SUCCESS);
    }

    /**
     * Get an user's friends.
     * @param email the email of the user.
     * @return the friends of the user.
     */

    @ApiMethod(name = "getUserFriends")
    public final ArrayList<User> getUserFriends(@Named("email") final String email) {
        //Find user
        User user = ofy().load().type(User.class).id(email).now();
        return user.getFriends();
    }

    /**
     * Get an user's pending friend requests.
     * @param email the email of the user.
     * @return the pending friend requests of the user.
     */

    @ApiMethod(name = "getUserFriendRequests")
    public final ArrayList<FriendRequest> getUserFriendRequests(@Named("email") final String email) {
        //Find user
        User user = ofy().load().type(User.class).id(email).now();
        return user.getPendingFriendRequests();
    }
}
