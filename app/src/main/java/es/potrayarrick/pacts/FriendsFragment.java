package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    public static final String ARG_FRIENDS = "friends";
    public static final String ARG_HIDE_REQUESTS_MENU = "hide requests menu";

    private ArrayList<User> mFriends;
    private boolean mHideFriendRequestsMenu;

    private ListView mFriendsView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friends a list with the mFriends of the user.
     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance(ArrayList<User> friends, boolean showRequestsMenu) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIENDS, friends);
        args.putBoolean(ARG_HIDE_REQUESTS_MENU, showRequestsMenu);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    @SuppressWarnings("unchecked") // This is for the casting warning.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mFriends = (ArrayList<User>) getArguments().getSerializable(ARG_FRIENDS);
            mHideFriendRequestsMenu = getArguments().getBoolean(ARG_HIDE_REQUESTS_MENU);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // We have a menu in this fragment.
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // UI elements
        mFriendsView = (ListView) view.findViewById(R.id.friends_list);

        // Get all mFriends names for the list.
        ArrayList <String> friendNames = new ArrayList<>();
        for (User friend : mFriends){
            friendNames.add(friend.getName());
        }

        // Set adapter
        FriendsArrayAdapter adapter = new FriendsArrayAdapter(view.getContext(),
                android.R.layout.simple_list_item_1, friendNames);
        mFriendsView.setAdapter(adapter);

        mFriendsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // TODO launch friend fragment.
            }

        });
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_friends_add:
                SendFriendRequestDialogFragment friendRequestDialogFragment = new SendFriendRequestDialogFragment();
                friendRequestDialogFragment.show(getFragmentManager(), "Fragment");
                return true;
            case R.id.menu_friends_requests:
                onMenuItemClick(OnFragmentInteractionListener.SHOW_FRIEND_REQUEST_FRAGMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear previous menu
        menu.clear();
        // Swap menu bars.
        inflater.inflate(R.menu.menu_friends, menu);

        // Hide friend requests menu if necessary.
        if(mHideFriendRequestsMenu){
            menu.findItem(R.id.menu_friends_requests).setVisible(false);
        }
    }

    public void onMenuItemClick(String action) {
        if (mListener != null) {
            mListener.onMenuClick(action);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        String SHOW_FRIEND_REQUEST_FRAGMENT = "show friend request fragment";

        public void onMenuClick(String action);
    }

    private class FriendsArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public FriendsArrayAdapter(Context context, int viewId, ArrayList <String> friendNames){
            super(context, viewId, friendNames);
            for (int i = 0; i < friendNames.size(); ++i) {
                mIdMap.put(friendNames.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
