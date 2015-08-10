package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
     * An argument name for {@link #newInstance(ArrayList<FriendRequest> friendRequests)} <code>friendRequests</code> parameter.
     */
    public static final String ARG_FRIEND_REQUESTS = "friend requests";

    /**
     * A list with the user's friend requests.
     */
    private ArrayList<FriendRequest> mFriendRequests;

    private ListView mFriendRequestView;

    private OnFriendRequestFragmentInteractionListener mListener;

    static ReceivedFriendRequestFragment newIntance (ArrayList<FriendRequest> friendRequests){
        ReceivedFriendRequestFragment fragment = new ReceivedFriendRequestFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIEND_REQUESTS, friendRequests);
        fragment.setArguments(args);
        return fragment;
    }

    public ReceivedFriendRequestFragment() {
        // Required empty public constructor
    }

    public interface OnFriendRequestFragmentInteractionListener {
        String ACCEPT_REQUEST = "accept";
        String REJECT_REQUEST = "reject";

        public void onFriendRequestInteraction (FriendRequest request, String message);
    }

    public boolean checkRequestCount (){
        return mFriendRequests.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked") // This is for the casting warning.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mFriendRequests = (ArrayList<FriendRequest>) getArguments().getSerializable(ARG_FRIEND_REQUESTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_received_friend_requests, container, false);

        // Add back button to the action bar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // UI elements
        mFriendRequestView = (ListView) view.findViewById(R.id.friend_request_listView);

        // Get friends name
        ArrayList<String> mFriendRequestSenderNames = new ArrayList<>();
        for (FriendRequest request: mFriendRequests){
            mFriendRequestSenderNames.add(request.getSender().getName() + " " + request.getSender().getSurname());
        }

        FriendRequestsArrayAdapter adapter = new FriendRequestsArrayAdapter(view.getContext(), mFriendRequestSenderNames);
        mFriendRequestView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void deleteRequest (FriendRequest request){
        mFriendRequests.remove(request);
    }


    private void onFriendRequestInteraction (FriendRequest request, String message){
        if (mListener != null){
            mListener.onFriendRequestInteraction(request, message);
        }
    }


    private class FriendRequestsArrayAdapter extends ArrayAdapter<String>{
        private final Context context;
        private final ArrayList<String> names;

        public FriendRequestsArrayAdapter (Context context, ArrayList<String> names){
            super(context, R.layout.list_element_received_friend_request, names);
            this.context = context;
            this.names = new ArrayList<>(names);

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_element_received_friend_request, parent, false);
            }

            // Populate the data
            TextView senderNameView = (TextView) convertView.findViewById(R.id.request_sender_name);
            senderNameView.setText(names.get(position));

            // Button callbacks
            Button acceptRequestButton = (Button) convertView.findViewById(R.id.accept_button);
            Button rejectRequestButton = (Button) convertView.findViewById(R.id.reject_button);

            acceptRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendRequest request = mFriendRequests.get(position);

                    // Tell the main activity to send info to backend.
                    onFriendRequestInteraction(request, OnFriendRequestFragmentInteractionListener.ACCEPT_REQUEST);
                }
            });

            rejectRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendRequest request = mFriendRequests.get(position);

                    // Tell the main activity to send info to backend.
                    onFriendRequestInteraction(request, OnFriendRequestFragmentInteractionListener.REJECT_REQUEST);
                }
            });

            return convertView;

        }
    }

}
