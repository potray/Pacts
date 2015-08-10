package es.potrayarrick.pacts;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;

import backend.pacts.potrayarrick.es.friends.Friends;
import backend.pacts.potrayarrick.es.friends.model.FriendRequest;
import backend.pacts.potrayarrick.es.friends.model.Message;
import backend.pacts.potrayarrick.es.friends.model.User;

public class MainActivity extends AppCompatActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        ProfileFragment.OnFragmentInteractionListener,
        FriendsFragment.OnFriendsFragmentInteractionListener,
        PactsFragment.OnFragmentInteractionListener,
        ReceivedFriendRequestFragment.OnFriendRequestFragmentInteractionListener{

    private static final String TAG = "MainActivity";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;



    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ProfileFragment mProfileFragment;
    private PactsFragment mPactsFragment;
    private FriendsFragment mFriendsFragment;
    private ReceivedFriendRequestFragment mReceivedFriendRequestFragment;

    private UserInfoTask mUserInfoTask;
    private ManageFriendRequestTask mManageFriendRequestTask;
    private static Friends mFriendsService = null;
    private String mEmail;

    private ArrayList<android.app.Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public void onNavigationDrawerItemSelected(int position) {
        // Update the main content by replacing fragments
        if (fragments != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments.get(position)).commit();

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
            }
            restoreActionBar();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section_pacts);
                break;
            case 2:
                mTitle = getString(R.string.title_section_profile);
                break;
            case 3:
                mTitle = getString(R.string.title_section_friends);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMenuClick(String action) {
        switch(action){
            case FriendsFragment.OnFriendsFragmentInteractionListener.SHOW_FRIEND_REQUEST_FRAGMENT:
                // Show friend requests fragment, putting the current fragment in the back stack.
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mReceivedFriendRequestFragment)
                        .addToBackStack(null).commit();
        }
    }

    @Override
    public void onFriendRequestInteraction(FriendRequest request, String message) {
        mManageFriendRequestTask.execute(new Pair<>(request,message));
    }

    private void setUpFriendService(){
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

    private class UserInfoTask extends AsyncTask <Void, Void, Boolean>{

        private final String mEmail;

        private UserInfoTask(String mEmail) {
            this.mEmail = mEmail;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            setUpFriendService();

            try {
                // Get user info (friends, requests...) and send them to the correct fragment.
                ArrayList<User> friends = new ArrayList<>(mFriendsService.getUserFriends(mEmail).execute().getItems());
                ArrayList<FriendRequest> requests = new ArrayList<>(mFriendsService.getUserFriendRequests(mEmail).execute().getItems());

                Bundle friendsFragmentBundle = new Bundle();
                Bundle friendRequestsFragmentBundle = new Bundle();

                friendsFragmentBundle.putSerializable(FriendsFragment.ARG_FRIENDS, friends);
                friendRequestsFragmentBundle.putSerializable(ReceivedFriendRequestFragment.ARG_FRIEND_REQUESTS, requests);
                friendRequestsFragmentBundle.putBoolean(FriendsFragment.ARG_HIDE_REQUESTS_MENU, requests.isEmpty());

                mFriendsFragment.setArguments(friendsFragmentBundle);
                mReceivedFriendRequestFragment.setArguments(friendRequestsFragmentBundle);

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private class ManageFriendRequestTask extends AsyncTask <Pair<FriendRequest, String>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Pair<FriendRequest, String>... params) {
            setUpFriendService();
            FriendRequest request = params[0].first;
            String response = params[0].second;

            try {
                Message message = mFriendsService.answerFriendRequest(request.getId(), response).execute();

                if (message.getSuccess()){
                    // Delete the request.
                    mReceivedFriendRequestFragment.deleteRequest(request);

                    // Check if friends fragment should hide requests fragment.
                    mFriendsFragment.setHideFriendRequestMenu(mReceivedFriendRequestFragment.checkRequestCount());

                    // We need to reattach the received friend requests fragment to update it.
                    getFragmentManager().beginTransaction()
                            .detach(mReceivedFriendRequestFragment)
                            .attach(mReceivedFriendRequestFragment)
                            .commit();

                    if (response.equals(ReceivedFriendRequestFragment.OnFriendRequestFragmentInteractionListener.ACCEPT_REQUEST)){
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
