package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import backend.pacts.potrayarrick.es.friends.model.FriendRequest;

/**
 * A fragment for managing friend requests.
 */
public class ReceivedFriendRequestFragment extends Fragment {
    /**
     * An argument name for {@link #newInstance(ArrayList)} <code>friendRequests</code> parameter.
     */
    public static final String ARG_FRIEND_REQUESTS = "friend requests";

    /**
     * A list with the user's friend requests.
     */
    private ArrayList<FriendRequest> mFriendRequests;

    /**
     * The fragment's interface to communicate with {@link MainActivity}.
     */
    private OnFriendRequestFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     * @param friendRequests A list with the received friend requests.
     * @return A new instance of fragment {@link ReceivedFriendRequestFragment}.
     */
    @SuppressWarnings("unused")
    static ReceivedFriendRequestFragment newInstance(final ArrayList<FriendRequest> friendRequests) {
        ReceivedFriendRequestFragment fragment = new ReceivedFriendRequestFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIEND_REQUESTS, friendRequests);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public ReceivedFriendRequestFragment() {
    }

    /**
     * An interface to communicate with {@link MainActivity}.
     */
    public interface OnFriendRequestFragmentInteractionListener {
        /**
         * Tells {@link MainActivity} to accept a friend request.
         */
        String ACCEPT_REQUEST = "accept";
        /**
         * Tells {@link MainActivity} to reject a friend request.
         */
        String REJECT_REQUEST = "reject";

        /**
         * Tells {@link MainActivity} to manage a friend request.
         * @param request the friend request to manage.
         * @param message either {@link #ACCEPT_REQUEST} or {@link #REJECT_REQUEST}.
         */
        void onFriendRequestInteraction(FriendRequest request, String message);
    }

    /**
     * Checks if {@link #mFriendRequests} is empty.
     * @return true if {@link #mFriendRequests} is empty, false if not.
     */
    public final boolean checkRequestCount() {
        return mFriendRequests.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked") // This is for the casting warning.
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mFriendRequests = (ArrayList<FriendRequest>) getArguments().getSerializable(ARG_FRIEND_REQUESTS);
        }
    }

    @Nullable
    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_received_friend_requests, container, false);

        // Add back button to the action bar
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            setHasOptionsMenu(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        // UI elements
        ListView mFriendRequestView = (ListView) view.findViewById(R.id.friend_request_listView);

        // Get friends name
        ArrayList<String> mFriendRequestSenderNames = new ArrayList<>();
        for (FriendRequest request: mFriendRequests) {
            mFriendRequestSenderNames.add(request.getSender().getName() + " " + request.getSender().getSurname());
        }

        FriendRequestsArrayAdapter adapter = new FriendRequestsArrayAdapter(view.getContext(), mFriendRequestSenderNames);
        mFriendRequestView.setAdapter(adapter);

        return view;
    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        try {
            mListener = (OnFriendRequestFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFriendRequestFragmentInteractionListener");
        }
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes a friend request from {@link #mFriendRequests}.
     * @param request the request to delete.
     */
    public final void deleteRequest(final FriendRequest request) {
        mFriendRequests.remove(request);
    }


    /**
     * Tells {@link #mListener} to communicate with {@link MainActivity}.
     * @param request a friend request to manage.
     * @param message either {@link es.potrayarrick.pacts.ReceivedFriendRequestFragment.OnFriendRequestFragmentInteractionListener#ACCEPT_REQUEST}
     *                or {@link es.potrayarrick.pacts.ReceivedFriendRequestFragment.OnFriendRequestFragmentInteractionListener#REJECT_REQUEST}.
     */
    private void onFriendRequestInteraction(final FriendRequest request, final String message) {
        if (mListener != null) {
            mListener.onFriendRequestInteraction(request, message);
        }
    }

    /**
     * An adapter for the friend request list.
     */
    private class FriendRequestsArrayAdapter extends ArrayAdapter<String> {
        /**
         * The names of the users who sent a request to this one.
         */
        private final ArrayList<String> names;

        /**
         * Default constructor.
         * @param context the context of the view.
         * @param names A list of names to populate {@link #names}
         */
        public FriendRequestsArrayAdapter(final Context context, final ArrayList<String> names) {
            super(context, R.layout.list_element_received_friend_request, names);
            this.names = new ArrayList<>(names);

        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            View view;
                if (convertView == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.list_element_received_friend_request, parent, false);
                } else {
                    view = convertView;
                }
            // Populate the data
            TextView senderNameView = (TextView) view.findViewById(R.id.request_sender_name);
            senderNameView.setText(names.get(position));

            // Button callbacks
            Button acceptRequestButton = (Button) view.findViewById(R.id.accept_button);
            Button rejectRequestButton = (Button) view.findViewById(R.id.reject_button);

            acceptRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FriendRequest request = mFriendRequests.get(position);

                    // Tell the main activity to send info to backend.
                    onFriendRequestInteraction(request, OnFriendRequestFragmentInteractionListener.ACCEPT_REQUEST);
                }
            });

            rejectRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FriendRequest request = mFriendRequests.get(position);

                    // Tell the main activity to send info to backend.
                    onFriendRequestInteraction(request, OnFriendRequestFragmentInteractionListener.REJECT_REQUEST);
                }
            });

            return view;

        }
    }

}
