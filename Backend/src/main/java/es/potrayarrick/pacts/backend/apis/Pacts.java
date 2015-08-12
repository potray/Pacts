package es.potrayarrick.pacts.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;

import java.util.ArrayList;

import javax.inject.Named;

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
     * @param senderEmail the sender email.
     * @param receiverEmail the receiver email.
     * @param pactName the name of the pact.
     * @param pactDescription the description of the pact.
     * @param pactType the type of the pact (if any).
     * @param isPromise whether the pact is a promise.
     */
    @ApiMethod(name = "sendPactRequest")
    public final void sendPactRequest (@Named("sender") final String senderEmail, @Named("receiver") final String receiverEmail,
                                       @Named ("name") final String pactName, @Named("description") final String pactDescription,
                                       @Named ("type") final String pactType, @Named ("isPromise") final boolean isPromise){

        // Get sender and receiver.
        User sender = ofy().load().type(User.class).id(senderEmail).now();
        User receiver = ofy().load().type(User.class).id(receiverEmail).now();


        // Create the pact and the request
        Pact pact = new Pact (pactName, pactDescription, sender, receiver);
        if (!isPromise){
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

        // Send the request to the user
        sender.sendPactRequest(request);
        receiver.receivePactRequest(request);

        ofy().save().entity(sender);
        ofy().save().entity(receiver);
    }

    /**
     * Get the received pact requests of an user.
     * @param email the user's email.
     * @return the list of the received pact requests.
     */
    @ApiMethod (name = "getPactRequests")
    public final ArrayList<PactRequest> getPactRequests (@Named ("email") final String email) {
        // Get user
        User user = ofy().load().type(User.class).id(email).now();
        return user.getReceivedPactRequests();
    }


    @ApiMethod (name = "getPacts")
    public final ArrayList<Pact> getPacts (@Named ("email") final String email) {
        // Get user
        User user = ofy().load().type(User.class).id(email).now();
        return user.getPacts();
    }

}
