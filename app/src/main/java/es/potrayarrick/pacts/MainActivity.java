package es.potrayarrick.pacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import backend.pacts.potrayarrick.es.friends.Friends;
import backend.pacts.potrayarrick.es.friends.model.FriendRequest;
import backend.pacts.potrayarrick.es.friends.model.Message;
import backend.pacts.potrayarrick.es.friends.model.User;
import backend.pacts.potrayarrick.es.pacts.Pacts;
import backend.pacts.potrayarrick.es.pacts.model.Pact;
import backend.pacts.potrayarrick.es.pacts.model.PactRequest;
import backend.pacts.potrayarrick.es.pacts.model.PactType;

/**
 * The main activity of the app.
 */
public class MainActivity extends AppCompatActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        ProfileFragment.OnFragmentInteractionListener,
        FriendsFragment.OnFriendsFragmentInteractionListener,
        PactsFragment.OnPactsFragmentInteractionListener,
        FriendRequestFragment.OnFriendRequestFragmentInteractionListener,
        FriendFragment.OnFriendFragmentInteractionListener,
        CreatePactFragment.OnCreatePactInteractionListener,
        CreatePactTypeDialogFragment.OnCreatePactTypeDialogFragmentInteractionListener,
        PactRequestsFragment.OnPactRequestsInteractionListener,
        PactFragment.OnPactFragmentInteractionListener {

    /**
     * A debugging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    /**
     * Logout drawer option (darn you CheckStyle and your magic number craziness...).
     */
    private static final int DRAWER_OPTION_LOGOUT = 3;
    /**
     * The friends backend service.
     */
    private static Friends mFriendsService = null;
    /**
     * The pacts backend service.
     */
    private static Pacts mPactsService = null;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * Used to store previous fragment title.
     */
    private Stack<String> mPreviousTitleStack;

//region Fragments

    /**
     * The profile fragment.
     */
    private ProfileFragment mProfileFragment;
    /**
     * The pacts fragment.
     */
    private PactsFragment mPactsFragment;
    /**
     * The friends fragment.
     */
    private FriendsFragment mFriendsFragment;
    /**
     * The friend fragment.
     */
    private FriendFragment mFriendFragment;
    /**
     * The create pact fragment.
     */
    private CreatePactFragment mCreatePactFragment;
    /**
     * The received friend requests fragment.
     */
    private FriendRequestFragment mFriendRequestFragment;
    /**
     * The received pact requests fragment.
     */
    private PactRequestsFragment mPactRequestsFragment;
    /**
     * The pact fragment.
     */
    private PactFragment mPactFragment;
//endregion


    /**
     * The task for retrieving user info from backend.
     */
    private UserInfoTask mUserInfoTask;
    /**
     * The task for sending friend requests responses to backend.
     */
    private ManageFriendRequestTask mManageFriendRequestTask;
    /**
     * The user email.
     */
    private String mEmail;

    /**
     * A list of fragment that are managed by the drawer.
     */
    private ArrayList<android.app.Fragment> mDrawerHandledFragments;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviousTitleStack = new Stack<>();

        //Create fragments
        mProfileFragment = new ProfileFragment();
        mPactsFragment = new PactsFragment();
        mFriendsFragment = new FriendsFragment();
        mFriendFragment = new FriendFragment();
        mFriendRequestFragment = new FriendRequestFragment();
        mCreatePactFragment = new CreatePactFragment();
        mPactRequestsFragment = new PactRequestsFragment();
        mPactFragment = new PactFragment();

        mDrawerHandledFragments = new ArrayList<>();
        mDrawerHandledFragments.add(mPactsFragment);
        mDrawerHandledFragments.add(mProfileFragment);
        mDrawerHandledFragments.add(mFriendsFragment);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up services
        mEmail = getSharedPreferences(Utils.PREFS_NAME, 0).getString(Utils.Strings.USER_EMAIL, "");
        mUserInfoTask = new UserInfoTask(mEmail);
        mUserInfoTask.execute();
        mManageFriendRequestTask = new ManageFriendRequestTask();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public final void onNavigationDrawerItemSelected(final int position) {
        // Update the main content by replacing fragments
        if (mDrawerHandledFragments != null) {
            if (position < mDrawerHandledFragments.size()) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mDrawerHandledFragments.get(position)).commit();
            }

            //Update action bar title
            switch (position) {
                case 0:
                    mTitle = getString(R.string.title_section_pacts);
                    break;
                case 1:
                    mTitle = getString(R.string.title_section_profile);
                    break;
                case 2:
                    mTitle = getString(R.string.title_section_friends);
                    break;
                case DRAWER_OPTION_LOGOUT:
                    // Show confirmation dialog
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.logout)
                            .setMessage(R.string.logout_confirmation)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                    logOut();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                    break;
                default:
                    break;
            }
            restoreActionBar();
        }
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public final void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            if (getFragmentManager().getBackStackEntryCount() == 1){
                // We start using the drawer again.
                mNavigationDrawerFragment.toggleDrawerUse(true);
            }
            // Pop fragment and restore previous title.
            getFragmentManager().popBackStack();
            mTitle = mPreviousTitleStack.pop();
            restoreActionBar();
        }
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public final void onMenuClick(final String action) {
        switch (action) {
            case FriendsFragment.OnFriendsFragmentInteractionListener.SHOW_FRIEND_REQUEST_FRAGMENT:
                hideDrawer(getString(R.string.title_manage_friend_requests));
                // Show friend requests fragment, putting the current fragment in the back stack.
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mFriendRequestFragment)
                        .addToBackStack(null).commit();
                break;
            default:
                break;
        }
    }

    //region Fragment interface implementations.

    @Override
    public final void onFriendClick(final User friend) {
        // Set the friend fragment arguments.
        Bundle friendFragmentArguments = new Bundle();
        HashMap<String, String> friendInfo = new HashMap<>();
        friendInfo.put(Utils.Strings.USER_NAME, friend.getName());
        friendInfo.put(Utils.Strings.USER_SURNAME, friend.getSurname());
        friendInfo.put(Utils.Strings.USER_EMAIL, friend.getEmail());
        friendFragmentArguments.putSerializable(FriendFragment.ARG_FRIEND_INFO, friendInfo);

        // TODO add pacts.
        mFriendFragment.setArguments(friendFragmentArguments);

        // Show the friend fragment.
        hideDrawer(friend.getName());
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mFriendFragment).addToBackStack(null).commit();
    }


    @Override
    public final void onCreatePactMenuPressed(final String receiverEmail) {
        // Set fragment arguments.
        Bundle arguments = mCreatePactFragment.getArguments();
        arguments.putString(CreatePactFragment.ARG_RECEIVER_EMAIL, receiverEmail);
        arguments.putString(CreatePactFragment.ARG_SENDER_EMAIL, mEmail);
        mCreatePactFragment.setArguments(arguments);

        // Show the create pact fragment.
        hideDrawer(getString(R.string.action_create_pact));
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mCreatePactFragment).addToBackStack(null).commit();
    }


    @Override
    public final void onFriendRequestInteraction(final FriendRequest request, final String message) {
        mManageFriendRequestTask.execute(new Pair<>(request, message));
    }

    @Override
    public final void onCreatePact() {
        // TODO send pact info to the pacts fragment.
        onBackPressed();
    }


    @Override
    public final void onCreatePactType(final String type) {
        mCreatePactFragment.addPactType(type);
    }

    @Override
    public void onMenuPactsRequests() {
        // Show the pact requests fragment.
        hideDrawer(getString(R.string.action_view_pact_requests));
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mPactRequestsFragment).addToBackStack(null).commit();
    }

    @Override
    public void onPact(Pact pact) {
        showPact(pact);
    }


    @Override
    public void onPactRequestItemPressed(Pact pact) {
        showPact(pact);
    }
//endregion


    /**
     * Restores the action bar, mainly for updating the title.
     */
    public final void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    /**
     * Sets up the friend service.
     */
    private void setUpFriendService() {
        if (mFriendsService == null) {   // Only do this once
            Friends.Builder builder;
            if (Utils.LOCAL_TESTING) {
                builder = new Friends.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(final AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
            } else {
                builder = new Friends.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null).setRootUrl("https://pacts-1027.appspot.com/_ah/api/");
            }

            mFriendsService = builder.build();
        }
    }

    /**
     * User logout.
     */
    private void logOut() {
        // Delete user info
        SharedPreferences preferences = getSharedPreferences(Utils.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Utils.Strings.USER_EMAIL);
        editor.remove(Utils.Strings.USER_NAME);
        editor.remove(Utils.Strings.USER_SURNAME);
        editor.putBoolean(Utils.Strings.USER_LOGGED_IN, false);

        editor.apply();

        // Send user to login activity
        finish();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Shows the pact fragment populated with a pact information.
     * @param pact the pact.
     */
    private void showPact (Pact pact) {
        // Set the pact fragment arguments and show it.
        hideDrawer(pact.getName());

        // We use an ArrayList to automatically serialize the pact.
        ArrayList<Pact> pactArgument = new ArrayList<>();
        pactArgument.add(pact);

        Bundle argsBundle = new Bundle();
        argsBundle.putSerializable(PactFragment.ARG_PACT, pactArgument);

        mPactFragment.setArguments(argsBundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mPactFragment).addToBackStack(null).commit();
    }

    /**
     * Hides the drawer, shows the back button and shows a new title.
     *
     * @param newMenuTitle the new title to show.
     */
    public final void hideDrawer(final String newMenuTitle) {
        // The fragment will have a back button instead of a drawer button.
        mNavigationDrawerFragment.toggleDrawerUse(false);
        // Change action bar title
        mPreviousTitleStack.push(mTitle.toString());
        mTitle = newMenuTitle;
        restoreActionBar();
    }

    /**
     * A fragment could require the user email, so this enables it to not use SharedPreferences.
     * @return the user's email.
     */
    public final String getUserEmail (){
        return mEmail;
    }

    @Override
    public void onAcceptPact(Pact pact) {
        PactActionAsyncTask task = new PactActionAsyncTask(pact, "ACCEPT");
        task.execute();
    }

    @Override
    public void onRejectPact(Pact pact) {
        PactActionAsyncTask task = new PactActionAsyncTask(pact, "REJECT");
        task.execute();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * A task to get the user info.
     */
    private final class UserInfoTask extends AsyncTask<Void, Void, Boolean> {
        /**
         * The email of the user.
         */
        private final String mEmail;

        /**
         * Default constructor.
         *
         * @param mEmail the email of the user.
         */
        private UserInfoTask(final String mEmail) {
            this.mEmail = mEmail;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            setUpFriendService();
            mPactsService = Utils.setUpPactsService();

            try {
                // Get user info (friends, requests, pact types...) and send them to the correct fragment.

                Bundle friendsFragmentBundle = new Bundle();
                Bundle friendRequestsFragmentBundle = new Bundle();
                Bundle createPactFragmentBundle = new Bundle();
                Bundle pactsFragmentBundle = new Bundle();
                Bundle pactRequestsFragmentBundle = new Bundle();

                Collection<User> friendsItems = mFriendsService.getUserFriends(mEmail).execute().getItems();
                Collection<FriendRequest> friendRequestsItems = mFriendsService.getUserFriendRequests(mEmail).execute().getItems();
                Collection<PactType> pactTypesItems = mPactsService.getUserPactTypes(mEmail).execute().getItems();
                Collection<Pact> pactsItems = mPactsService.getPacts(mEmail).execute().getItems();
                Collection<PactRequest> pactRequestsItems = mPactsService.getPactRequests(mEmail).execute().getItems();

                if (friendsItems != null) {
                    ArrayList<User> friends = new ArrayList<>(friendsItems);
                    friendsFragmentBundle.putSerializable(FriendsFragment.ARG_FRIENDS, friends);
                }

                if (friendRequestsItems != null) {
                    ArrayList<FriendRequest> requests = new ArrayList<>(friendRequestsItems);
                    Log.d(TAG, "doInBackground - requests = " + requests.toString());
                    friendRequestsFragmentBundle.putSerializable(FriendRequestFragment.ARG_FRIEND_REQUESTS, requests);

                    // Sometimes friendRequestsItems = [{}], so request will be created, and it's first element
                    // will be empty. We need to check for that.
                    boolean emptyRequests = friendRequestsItems.isEmpty() || (requests.size() == 1 && requests.get(0).isEmpty());
                    Log.d(TAG, "doInBackground  - emptyRequests = " + String.valueOf(emptyRequests));

                    friendsFragmentBundle.putBoolean(FriendsFragment.ARG_HIDE_REQUESTS_MENU, emptyRequests);
                } else {
                    // Hide the menu anyways
                    Log.d(TAG, "doInBackground: hiding friend request menu because friendRequestItems is null");
                    friendsFragmentBundle.putBoolean(FriendsFragment.ARG_HIDE_REQUESTS_MENU, true);
                }

                if (pactTypesItems != null) {
                    // We need an ArrayList<String> instead of an ArrayList<PactType>
                    ArrayList<String> pactTypes = new ArrayList<>();
                    Log.d(TAG, "doInBackground - pactTypesItems = " + pactTypesItems.toString());

                    for (PactType type : pactTypesItems) {
                        pactTypes.add(type.getType());
                    }
                    createPactFragmentBundle.putSerializable(CreatePactFragment.ARG_PACT_TYPES, pactTypes);
                }

                if (pactsItems != null && !pactsItems.isEmpty()) {
                    ArrayList<Pact> pacts = new ArrayList<>();
                    Log.d(TAG, "doInBackground - pactsItems = " + pactsItems.toString());

                    for (Pact pact : pactsItems){
                        pacts.add(pact);
                    }

                    pactsFragmentBundle.putSerializable(PactsFragment.ARG_PACTS, pacts);
                }

                if (pactRequestsItems != null && !pactRequestsItems.isEmpty()) {
                    ArrayList<PactRequest> pactRequests = new ArrayList<>();
                    Log.d(TAG, "doInBackground - We have pact requests: " + pactRequestsItems.toString());

                    pactRequests.addAll(pactRequestsItems);

                    pactRequestsFragmentBundle.putSerializable(PactRequestsFragment.ARG_PACT_REQUESTS, pactRequests);

                    pactsFragmentBundle.putBoolean(PactsFragment.ARG_HIDE_PACT_REQUESTS_MENU, false);
                } else {
                    Log.d(TAG, "doInBackground - No pact requests.");
                    pactsFragmentBundle.putBoolean(PactsFragment.ARG_HIDE_PACT_REQUESTS_MENU, true);
                }

                mFriendsFragment.setArguments(friendsFragmentBundle);
                mFriendRequestFragment.setArguments(friendRequestsFragmentBundle);
                mCreatePactFragment.setArguments(createPactFragmentBundle);
                mPactsFragment.setArguments(pactsFragmentBundle);
                mPactRequestsFragment.setArguments(pactRequestsFragmentBundle);

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //Load the pacts fragment
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, mDrawerHandledFragments.get(0)).commit();
        }
    }

    /**
     * A task for managing friend requests.
     */
    private class ManageFriendRequestTask extends AsyncTask<Pair<FriendRequest, String>, Void, Boolean> {
        @Override
        @SafeVarargs
        protected final Boolean doInBackground(final Pair<FriendRequest, String>... params) {
            setUpFriendService();
            FriendRequest request = params[0].first;
            String response = params[0].second;

            try {
                Message message = mFriendsService.answerFriendRequest(request.getId(), response).execute();

                if (message.getSuccess()) {
                    // Delete the request.
                    mFriendRequestFragment.deleteRequest(request);

                    // Check if friends fragment should hide requests fragment.
                    mFriendsFragment.setHideFriendRequestMenu(mFriendRequestFragment.checkRequestCount());

                    // We need to reattach the received friend requests fragment to update it.
                    getFragmentManager().beginTransaction()
                            .detach(mFriendRequestFragment)
                            .attach(mFriendRequestFragment)
                            .commit();

                    if (response.equals(FriendRequestFragment.OnFriendRequestFragmentInteractionListener.ACCEPT_REQUEST)) {
                        // Add a new friend
                        mFriendsFragment.addFriend(request.getSender());
                        return true;
                        // We don't need to reattach this fragment since we don't need to redraw it immediately.
                    } else {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean requestAccepted) {
            Toast toast;

            if (requestAccepted != null) {
                if (requestAccepted) {
                    toast = Toast.makeText(getApplicationContext(), R.string.info_user_request_accepted, Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(getApplicationContext(), R.string.info_user_request_rejected, Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        }
    }

    private class PactActionAsyncTask extends AsyncTask<Void, Void, Void>{

        private Pact pact;
        private String action;

        protected PactActionAsyncTask (Pact pact, String action) {
            this.pact = pact;
            this.action = action;
        }


        @Override
        protected Void doInBackground(Void... params) {
            // Set up the pact service (it could be null).
            mPactsService = Utils.setUpPactsService();

            try {
                mPactsService.pactAction(pact.getId(), action).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            boolean hidePactRequestButton = false;
            switch (action){
                case "ACCEPT":
                    hidePactRequestButton = mPactRequestsFragment.deletePactRequest(pact);
                    mPactsFragment.addPact(pact);
                    mPactFragment.pactRequestAnswered(true);

                    break;
                case "REJECT":
                    hidePactRequestButton = mPactRequestsFragment.deletePactRequest(pact);
                    mPactFragment.pactRequestAnswered(false);
                    break;
                case "FULFILL":
                    break;
            }

            if (hidePactRequestButton){
                mPactsFragment.setHidePactRequestButton(true);
            }
        }
    }
}
