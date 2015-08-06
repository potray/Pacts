package es.potrayarrick.pacts.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;

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
    public static final String ACCEPT = "accept friend request";
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
        // Get both users. Note: it's not necessary to check if both sender and receiver exists since
        // it's not possible to send a request to someone who doesn't exist.
        User sender = ofy().load().type(User.class).id(senderEmail).now();
        User receiver = ofy().load().type(User.class).id(receiverEmail).now();

        Key<User> senderKey = Key.create(sender);
        Key<User> receiverKey = Key.create(receiver);

        //Create the request
        FriendRequest request = new FriendRequest(senderKey, receiverKey);
        ofy().save().entity(request);
        Key<FriendRequest> requestKey = Key.create(request);
        sender.sendFriendRequest(requestKey);
        receiver.receiveFriendRequest(requestKey);

        //Save entities
        ofy().save().entity(sender);
        ofy().save().entity(receiver);

        return new Message(Message.SUCCESS);
    }


    @ApiMethod(name = "answerFriendRequest")
    public final Message answerFriendRequest(@Named ("requestKey") final String requestKey,
                                             @Named ("answer") final String answer) {
        //Get the request
        //FriendRequest request = ofy().load().type(FriendRequest.class).
        return new Message(Message.SUCCESS);
    }
}
