package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import backend.pacts.potrayarrick.es.friends.model.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFriendsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    /**
     * A tag for debugging.
     */
    private static final String TAG = "FriendsFragment";
    /**
     * An argument name for {@link #newInstance(ArrayList, boolean)} <code>friends</code> parameter.
     */
    public static final String ARG_FRIENDS = "friends";
    /**
     * An argument name for {@link #newInstance(ArrayList, boolean)} <code>showRequestsMenu</code> parameter.
     */
    public static final String ARG_HIDE_REQUESTS_MENU = "hide requests menu";

    /**
     * A list containing the user's friends.
     */
    private ArrayList<User> mFriends;
    /**
     * True if the friend requests menu button should be hidden.
     */
    private boolean mHideFriendRequestsMenu;

    /**
     * The fragment's interface to communicate with {@link MainActivity}.
     */
    private OnFriendsFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friends a list with the user's friends.
     * @param showRequestsMenu whether or not the friend requests menu button should be hidden.
     * @return A new instance of fragment FriendsFragment.
     */
    @SuppressWarnings("unused")
    public static FriendsFragment newInstance(final ArrayList<User> friends, final boolean showRequestsMenu) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIENDS, friends);
        args.putBoolean(ARG_HIDE_REQUESTS_MENU, showRequestsMenu);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public FriendsFragment() {
    }

    /**
     * An interface to communicate with {@link MainActivity}.
     */
    public interface OnFriendsFragmentInteractionListener {
        /**
         * Tells {@link MainActivity} to show the friend requests fragment.
         */
        String SHOW_FRIEND_REQUEST_FRAGMENT = "show friend request fragment";

        /**
         * Tells {@link MainActivity} to do an action.
         * @param action the action to do.
         */
        void onMenuClick(String action);

        /**
         * Tells {@link MainActivity} to show the friend fragment.
         * @param friend the friend with the info to populate the fragment.
         */
        void onFriendClick (User friend);
    }

    /**
     * Add a friend to {@link #mFriends}.
     * @param friend the friend to add.
     */
    public final void addFriend(final User friend) {
        mFriends.add(friend);
    }

    /**
     * Delete a friend from {@link #mFriends}.
     * @param friend the friend to Delete.
     */
    public final void deleteFriend(final User friend) {
        mFriends.remove(friend);
    }

    /**
     * Sets {@link #mHideFriendRequestsMenu}.
     * @param hide the new {@link #mHideFriendRequestsMenu}.
     */
    public final void setHideFriendRequestMenu(final boolean hide) {
        Log.d(TAG, "setHideFriendRequestMenu  - " + String.valueOf(hide));
        mHideFriendRequestsMenu = hide;
    }

    @Override
    @SuppressWarnings("unchecked") // This is for the casting warning.
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriends = new ArrayList<>();
        // Null checks.
        if (getArguments() != null) {
            ArrayList<User> argFriendsList = (ArrayList<User>) getArguments().getSerializable(ARG_FRIENDS);
            if (argFriendsList != null) {
                mFriends.addAll(argFriendsList);
            }
            mHideFriendRequestsMenu = getArguments().getBoolean(ARG_HIDE_REQUESTS_MENU);
            Log.d(TAG, "onCreate  - mHideFriendRequestsMenu = " + String.valueOf(mHideFriendRequestsMenu));
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // We have a menu in this fragment.
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // UI elements
        ListView mFriendsView = (ListView) view.findViewById(R.id.friends_list);

        // Get all mFriends names for the list.
        ArrayList<String> friendNames = new ArrayList<>();
        if (mFriends != null) {
            for (User friend : mFriends) {
                friendNames.add(friend.getName());
            }
        }

        // Set adapter
        FriendsArrayAdapter adapter = new FriendsArrayAdapter(view.getContext(),
                android.R.layout.simple_list_item_1, friendNames);
        mFriendsView.setAdapter(adapter);

        mFriendsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                mListener.onFriendClick(mFriends.get(position));
            }

        });
        return view;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_friends_add:
                SendFriendRequestDialogFragment friendRequestDialogFragment = new SendFriendRequestDialogFragment();
                friendRequestDialogFragment.show(getFragmentManager(), "Fragment");
                return true;
            case R.id.menu_friends_requests:
                onFriendRequestsMenuItemClick(OnFriendsFragmentInteractionListener.SHOW_FRIEND_REQUEST_FRAGMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public final void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear previous menu
        menu.clear();
        // Swap menu bars.
        inflater.inflate(R.menu.menu_friends, menu);

        // Hide friend requests menu if necessary.
        if (mHideFriendRequestsMenu) {
            Log.d(TAG, "onCreateOptionsMenu: friend requests menu");
            menu.findItem(R.id.menu_friends_requests).setVisible(false);
        }
    }


    /**
     * Tells {@link #mListener} to communicate with {@link MainActivity}.
     * @param action the action {@link MainActivity} should perform.
     */
    public final void onFriendRequestsMenuItemClick(final String action) {
        if (mListener != null) {
            mListener.onMenuClick(action);
        }
    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);

        // Control whether or not the activity implements the interface.
        try {
            mListener = (OnFriendsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFriendsFragmentInteractionListener");
        }
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * The adapter for the friends list.
     */
    private class FriendsArrayAdapter extends ArrayAdapter<String> {

        /**
         * A temporal {@link HashMap} for showing the friends' names.
         */
        private HashMap<String, Integer> mIdMap = new HashMap<>();

        /**
         * Default constructor.
         * @param context the context of the view.
         * @param viewId the id of the layout to inflate.
         * @param friendNames a list with the user's friends' names.
         */
        public FriendsArrayAdapter(final Context context, final int viewId, final ArrayList<String> friendNames) {
            super(context, viewId, friendNames);
            for (int i = 0; i < friendNames.size(); ++i) {
                mIdMap.put(friendNames.get(i), i);
            }
        }

        @Override
        public long getItemId(final int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
