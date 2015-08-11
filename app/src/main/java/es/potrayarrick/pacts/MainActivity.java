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

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import backend.pacts.potrayarrick.es.friends.Friends;
import backend.pacts.potrayarrick.es.friends.model.FriendRequest;
import backend.pacts.potrayarrick.es.friends.model.Message;
import backend.pacts.potrayarrick.es.friends.model.User;

/**
 * The main activity of the app.
 */
public class MainActivity extends AppCompatActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        ProfileFragment.OnFragmentInteractionListener,
        FriendsFragment.OnFriendsFragmentInteractionListener,
        PactsFragment.OnFragmentInteractionListener,
        ReceivedFriendRequestFragment.OnFriendRequestFragmentInteractionListener {

    /**
     * A debugging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

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
     * The received friend requests fragment.
     */
    private ReceivedFriendRequestFragment mReceivedFriendRequestFragment;

    /**
     * The task for retrieving user info from backend.
     */
    private UserInfoTask mUserInfoTask;
    /**
     * The task for sending friend requests responses to backend.
     */
    private ManageFriendRequestTask mManageFriendRequestTask;
    /**
     * The friends backend service.
     */
    private static Friends mFriendsService = null;


    /**
     * The user email.
     */

    private String mEmail;

    /**
     * A list of fragment that are managed by the drawer.
     */
    private ArrayList<android.app.Fragment> fragments;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create fragments
        mProfileFragment = new ProfileFragment();
        mPactsFragment = new PactsFragment();
        mFriendsFragment = new FriendsFragment();
        mReceivedFriendRequestFragment = new ReceivedFriendRequestFragment();

        fragments = new ArrayList<>();
        fragments.add(mPactsFragment);
        fragments.add(mProfileFragment);
        fragments.add(mFriendsFragment);

        mNavigationDrawerFragment = (NavigationDrawerFragment)  getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        //Load the pacts fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments.get(0)).commit();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Set up services
        mEmail = getSharedPreferences(Utils.PREFS_NAME, 0).getString(Utils.Strings.USER_EMAIL, "");
        mUserInfoTask = new UserInfoTask(mEmail);
        mUserInfoTask.execute();
        mManageFriendRequestTask = new ManageFriendRequestTask();
    }

    @Override
    public final void onNavigationDrawerItemSelected(final int position) {
        // Update the main content by replacing fragments
        if (fragments != null) {
            if (position < fragments.size()) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments.get(position)).commit();
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
                case 3:
                    // Show confirmation dialog
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.logout)
                            .setMessage(R.string.logout_confirmation)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            mNavigationDrawerFragment.toggleDrawerUse(true);
            getFragmentManager().popBackStack();
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
    public void onFragmentInteraction(final Uri uri) {

    }

    @Override
    public final void onMenuClick(final String action) {
        switch (action) {
            case FriendsFragment.OnFriendsFragmentInteractionListener.SHOW_FRIEND_REQUEST_FRAGMENT:
                // The fragment will have a back button instead of a drawer button.
                mNavigationDrawerFragment.toggleDrawerUse(false);
                // Change action bar title
                mTitle = getString(R.string.title_manage_friend_requests);
                // Show friend requests fragment, putting the current fragment in the back stack.
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mReceivedFriendRequestFragment)
                        .addToBackStack(null).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public final void onFriendRequestInteraction(final FriendRequest request, final String message) {
        mManageFriendRequestTask.execute(new Pair<>(request, message));
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

    private void logOut(){
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
     * A task to get the user info.
     */
    private final class UserInfoTask extends AsyncTask<Void, Void, Boolean> {
        /**
         * The email of the user.
         */
        private final String mEmail;

        /**
         * Deffault constructor.
         * @param mEmail the email of the user.
         */
        private UserInfoTask(final String mEmail) {
            this.mEmail = mEmail;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            setUpFriendService();

            try {
                // Get user info (friends, requests...) and send them to the correct fragment.

                Bundle friendsFragmentBundle = new Bundle();
                Bundle friendRequestsFragmentBundle = new Bundle();

                Collection<User> friendsItems = mFriendsService.getUserFriends(mEmail).execute().getItems();
                Collection<FriendRequest> friendRequestsItems = mFriendsService.getUserFriendRequests(mEmail).execute().getItems();

                if (friendsItems != null) {
                    ArrayList<User> friends = new ArrayList<>(friendsItems);
                    friendsFragmentBundle.putSerializable(FriendsFragment.ARG_FRIENDS, friends);
                }

                if (friendRequestsItems != null){
                    ArrayList<FriendRequest> requests = new ArrayList<>();
                    friendRequestsFragmentBundle.putSerializable(ReceivedFriendRequestFragment.ARG_FRIEND_REQUESTS, requests);
                    friendRequestsFragmentBundle.putBoolean(FriendsFragment.ARG_HIDE_REQUESTS_MENU, friendRequestsItems.isEmpty());
                } else {
                    // Hide the menu anyways
                    Log.d(TAG, "doInBackground: hiding friend request menu because friendRequestItems is null");
                    friendsFragmentBundle.putBoolean(FriendsFragment.ARG_HIDE_REQUESTS_MENU, true);
                }

                mFriendsFragment.setArguments(friendsFragmentBundle);
                mReceivedFriendRequestFragment.setArguments(friendRequestsFragmentBundle);

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
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
                    mReceivedFriendRequestFragment.deleteRequest(request);

                    // Check if friends fragment should hide requests fragment.
                    mFriendsFragment.setHideFriendRequestMenu(mReceivedFriendRequestFragment.checkRequestCount());

                    // We need to reattach the received friend requests fragment to update it.
                    getFragmentManager().beginTransaction()
                            .detach(mReceivedFriendRequestFragment)
                            .attach(mReceivedFriendRequestFragment)
                            .commit();

                    if (response.equals(ReceivedFriendRequestFragment.OnFriendRequestFragmentInteractionListener.ACCEPT_REQUEST)) {
                        // Add a new friend
                        mFriendsFragment.addFriend(request.getSender());
                        // We don't need to reattach this fragment since we don't need to redraw it immediately.
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
