package es.potrayarrick.pacts;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;

import backend.pacts.potrayarrick.es.friends.Friends;
import backend.pacts.potrayarrick.es.friends.model.User;

public class MainActivity extends AppCompatActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        ProfileFragment.OnFragmentInteractionListener,
        FriendsFragment.OnFragmentInteractionListener,
        PactsFragment.OnFragmentInteractionListener{

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

    private UserInfoTask mUserInfoTask;
    private static Friends friendsService = null;
    private String mEmail;

    private ArrayList<android.app.Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "On create!");

        // TODO delete this, it's just for testing.
        backend.pacts.potrayarrick.es.friends.model.User user = new backend.pacts.potrayarrick.es.friends.model.User();

        //Create fragments
        mProfileFragment = new ProfileFragment();
        mPactsFragment = new PactsFragment();
        mFriendsFragment = new FriendsFragment();

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

        Log.d("Main", "Executed async task");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Update the main content by replacing fragments
        Log.d("main", "onNavigationDrawerItemSelected " + position);
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

    private class UserInfoTask extends AsyncTask <Void, Void, Boolean>{

        private final String mEmail;

        private UserInfoTask(String mEmail) {
            this.mEmail = mEmail;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("Main - Asynctask", "Background!");
            // Get user friends
            if (friendsService == null) {   // Only do this once
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

                friendsService = builder.build();
            }

            try {
                ArrayList<User> friends = new ArrayList<>(friendsService.getUserFriends(mEmail).execute().getItems());
                Bundle friendsFragmentBundle = new Bundle();
                friendsFragmentBundle.putSerializable("friends", friends);
                mFriendsFragment.setArguments(friendsFragmentBundle);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
