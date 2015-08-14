package es.potrayarrick.pacts.backend.models;

import com.google.appengine.repackaged.org.codehaus.jackson.annotate.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;

import static es.potrayarrick.pacts.backend.OfyService.ofy;

/**
 * The user of the application.
 */
@Entity
public class User {
    /**
     * The email of the user.
     */
    @Id
    private String email;

    /**
     * The password of the user.
     */
    @JsonIgnore
    private String password;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The surname of the user.
     */
    private String surname;

    /**
     * The friends of the user.
     */
    @JsonIgnore
    private ArrayList<Key<User>> friends = new ArrayList<>();

    /**
     * The friend requests the user has sent.
     */
    private ArrayList<Key<FriendRequest>> sentFriendRequests = new ArrayList<>();

    /**
     * The friend requests the user has received.
     */
    private ArrayList<Key<FriendRequest>> receivedFriendRequests = new ArrayList<>();

    /**
     * The user's pacts.
     */
    private ArrayList<Key<Pact>> pacts = new ArrayList<>();

    /**
     * The pact requests the user has sent.
     */
    private ArrayList<Key<PactRequest>> sentPactRequests = new ArrayList<>();

    /**
     * The pact requests the user has received.
     */
    private ArrayList<Key<PactRequest>> receivedPactRequests = new ArrayList<>();

    /**
     * The pact types the user has used.
     */
    private ArrayList<Key<PactType>> usedPactTypes = new ArrayList<>();

    /**
     * No-arg constructor for objectify.
     */
    @SuppressWarnings("unused")
    public User() { }

    /**
     * Basic constructor.
     * @param newUserEmail user email.
     * @param newUserPassword user password.
     */
    public User(final String newUserEmail, final String newUserPassword) {
        this.email = newUserEmail;
        this.password = newUserPassword;
    }

    /**
     * Get user email.
     * @return the email of the user.
     */
    @SuppressWarnings("unused") // The client uses this method.
    public final String getEmail() {
        return email;
    }

    /**
     * Get user password.
     * @return the password of the user.
     */
    public final String getPassword() {
        return password;
    }

    /**
     * Get user surname.
     * @return the surname of the user.
     */
    @SuppressWarnings("unused") // The client uses this method.
    public final String getSurname() {
        return surname;
    }

    /**
     * Set user surname.
     * @param surname the new surname of the user.
     */
    public final void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * Get user name.
     * @return the name of the user.
     */
    @SuppressWarnings("unused") // The client uses this method.
    public final String getName() {
        return name;
    }

    /**
     * Set user name.
     * @param name the new name of the user.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Adds a new friend to the friend list.
     * @param friend the key of new friend to add.
     */
    public final void addFriend(final Key<User> friend) {
        friends.add(friend);
    }

    /**
     * Adds a new friend request to the sent ones.
     * @param request the key of the sent request.
     */
    public final void sendFriendRequest(final Key<FriendRequest> request) {
        sentFriendRequests.add(request);
    }

    /**
     * Adds a new friend request to the received ones.
     * @param request the key of the received request.
     */
    public final void receiveFriendRequest(final Key<FriendRequest> request) {
        receivedFriendRequests.add(request);
    }

    /**
     * Deletes a sent friend request.
     * @param request the key of the request to delete.
     */
    public final void deleteSentFriendRequest(final Key<FriendRequest> request) {
        sentFriendRequests.remove(request);
    }
    /**
     * Deletes a received friend request.
     * @param request the key of the request to delete.
     */
    public final void deleteReceivedFriendRequest(final Key<FriendRequest> request) {
        receivedFriendRequests.remove(request);
    }

    /**
     * Checks if an user is a friend of this.
     * @param user the user to check.
     * @return true if both users are friends.
     */
    public final boolean isFriendOf(final User user) {
        return friends.contains(Key.create(user));
    }

    /**
     * Gets the friends of the user.
     * @return a list of User.
     */
    @JsonIgnore
    public final ArrayList<User> getFriends() {
        ArrayList<User> friends = new ArrayList<>();

        for (Key<User> key:this.friends) {
            friends.add(ofy().load().key(key).now());
        }

        return friends;
    }

    /**
     * Gets the pending friend requests of the user.
     * @return a list of friend requests.
     */
    @JsonIgnore
    public final ArrayList<FriendRequest> getPendingFriendRequests() {
        ArrayList<FriendRequest> pendingRequests = new ArrayList<>();

        for (Key<FriendRequest> key : receivedFriendRequests) {
            pendingRequests.add(ofy().load().key(key).now());
        }

        return pendingRequests;
    }

    public final ArrayList<PactRequest> getReceivedPactRequests() {
        ArrayList<PactRequest> requests = new ArrayList<>();

        for (Key<PactRequest> key : receivedPactRequests) {
            requests.add(ofy().load().key(key).now());
        }

        return requests;
    }

    public final ArrayList<Pact> getPacts() {
        ArrayList<Pact> pacts = new ArrayList<>();

        for (Key<Pact> key : this.pacts){
            pacts.add(ofy().load().key(key).now());
        }

        return pacts;
    }

    /**
     * Adds a new pact type to the list.
     * @param type the type to add.
     */
    public final void addPactType (PactType type) {
        Key<PactType> typeKey = Key.create(type);

        if (!usedPactTypes.contains(typeKey)) {
            usedPactTypes.add(typeKey);
        }
    }

    public ArrayList<PactType> getPactTypes () {
        ArrayList<PactType> types = new ArrayList<>();
        for (Key<PactType> key : usedPactTypes){
            types.add(ofy().load().key(key).now());
        }
        return types;
    }

    /**
     * Adds a new pact request to the sent ones.
     * @param request the request to add.
     */
    public final void sendPactRequest (PactRequest request) {
        sentPactRequests.add(Key.create(request));
    }

    /**
     * Adds a new pact request to the received ones.
     * @param request the request to add.
     */
    public final void receivePactRequest (PactRequest request) {
        receivedPactRequests.add(Key.create(request));
    }

    /**
     * Deletes a sent pact requests.
     * @param request the request to delete.
     */
    public final void deleteSentPactRequest (PactRequest request) {
        sentPactRequests.remove(Key.create(request));
    }

    /**
     * Deletes a received pact request.
     * @param request the request to delete.
     */
    public final void deleteReceivedPactRequest (PactRequest request) {
        receivedPactRequests.remove(Key.create(request));
    }
}
