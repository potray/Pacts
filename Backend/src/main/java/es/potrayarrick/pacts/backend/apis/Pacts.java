package es.potrayarrick.pacts.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;

import java.util.ArrayList;

import javax.inject.Named;

import es.potrayarrick.pacts.backend.Utils.Message;
import es.potrayarrick.pacts.backend.models.Pact;
import es.potrayarrick.pacts.backend.models.PactRequest;
import es.potrayarrick.pacts.backend.models.PactType;
import es.potrayarrick.pacts.backend.models.User;

import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * The API for managing pacts.
 */
@Api(name = "pacts",
        version = "0.1",
        namespace = @ApiNamespace(
                ownerDomain = "es.potrayarrick.pacts.backend",
                ownerName = "es.potrayarrick.pacts.backend",
                packagePath = ""
        ))
public class Pacts {
    /**
     * Send a pact request to a user.
     *
     * @param senderEmail     the sender email.
     * @param receiverEmail   the receiver email.
     * @param pactName        the name of the pact.
     * @param pactDescription the description of the pact.
     * @param pactType        the type of the pact (if any).
     * @param isPromise       whether the pact is a promise.
     */
    @ApiMethod(name = "sendPactRequest")
    public final void sendPactRequest(@Named("sender") final String senderEmail, @Named("receiver") final String receiverEmail,
                                      @Named("name") final String pactName, @Named("description") final String pactDescription,
                                      @Named("type") final String pactType, @Named("isPromise") final boolean isPromise) {

        // Get sender and receiver.
        User sender = ofy().load().type(User.class).id(senderEmail).now();
        User receiver = ofy().load().type(User.class).id(receiverEmail).now();


        // Create the pact and the request
        Pact pact = new Pact(pactName, pactDescription, sender, receiver);
        if (!isPromise) {
            // Check if a pact type should be created.
            PactType type = ofy().load().type(PactType.class).id(pactType).now();

            if (type == null) {
                type = new PactType(pactType);
                ofy().save().entity(type).now();
            }

            // Add type to the sender, even without the receiver accepting the request.
            sender.addPactType(type);

            pact.setType(type);
        }
        pact.setPromise(isPromise);

        // We need to save the pact before the request constructor creates the key.
        ofy().save().entity(pact).now();

        PactRequest request = new PactRequest(Key.create(sender), Key.create(receiver), pact);
        ofy().save().entity(request).now();

        // Send the request to the user.
        sender.sendPactRequest(request);
        receiver.receivePactRequest(request);

        // The sender has the pact.
        sender.addPact(pact);

        ofy().save().entity(sender);
        ofy().save().entity(receiver);
    }

    @ApiMethod(name = "pactAction")
    public final Message pactAction (@Named("pactId") Long pactId,
                                  @Named("action") String action){
        // Get pact.
        Pact pact = ofy().load().type(Pact.class).id(pactId).now();

        switch (action){
            case "ACCEPT":
                // Get the sender and the receiver of the pact requests.
                Key<Pact> pactKey = Key.create(pact);
                PactRequest request = ofy().load().type(PactRequest.class).filter("pact", pactKey).first().now();

                User sender = request.getSender();
                User receiver = request.getReceiver();

                // Add pact to the receiver.
                receiver.addPact(pact);

                // Delete the request
                sender.deleteSentPactRequest(request);
                receiver.deleteReceivedPactRequest(request);
                ofy().delete().entity(request).now();
                ofy().save().entity(sender).now();
                ofy().save().entity(receiver).now();

                pact.accept();
                break;
            case "CANCEL":
                break;
            case "FULFILL":
                break;
        }

        ofy().save().entity(pact).now();
        return new Message(Message.SUCCESS);

    }

    /**
     * Get the received pact requests of an user.
     *
     * @param email the user's email.
     * @return the list of the received pact requests.
     */
    @ApiMethod(name = "getPactRequests")
    public final ArrayList<PactRequest> getPactRequests(@Named("email") final String email) {
        // Get user
        User user = ofy().load().type(User.class).id(email).now();
        return user.getReceivedPactRequests();
    }

    /**
     * Get the pacts of an user.
     *
     * @param email the user's email.
     * @return the list of the user's pacts.
     */
    @ApiMethod(name = "getPacts")
    public final ArrayList<Pact> getPacts(@Named("email") final String email) {
        // Get user
        User user = ofy().load().type(User.class).id(email).now();
        return user.getPacts();
    }

    /**
     * Create a new pact type and add it to the user's pact types.
     * If the pact already exists it just adds it to the user's pact types.
     *
     * @param email   the email of the user.
     * @param newType the new pact type.
     */
    @ApiMethod(name = "createPactType")
    public final void createPactType(@Named("email") final String email,
                                     @Named("type") final String newType) {
        // Get user and type.
        User user = ofy().load().type(User.class).id(email).now();
        PactType type = ofy().load().type(PactType.class).id(newType).now();

        // Create the type if necessary.
        if (type == null) {
            type = new PactType(newType);
            ofy().save().entity(type).now();
        }

        user.addPactType(type);
        ofy().save().entity(user).now();
    }

    /**
     * Get the pact types of an user.
     *
     * @param email the email of the user.
     * @return a list with all the pact types of the user.
     */
    @ApiMethod(name = "getUserPactTypes")
    public final ArrayList<PactType> getUserPactTypes(@Named("email") final String email) {
        // Get user
        User user = ofy().load().type(User.class).id(email).now();

        return user.getPactTypes();
    }
}
