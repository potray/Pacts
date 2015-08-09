package es.potrayarrick.pacts;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import backend.pacts.potrayarrick.es.friends.model.FriendRequest;

public class ReceivedFriendRequestFragment extends Fragment{
    public static final String ARG_FRIEND_REQUESTS = "friend requests";

    private ArrayList<FriendRequest> mFriendRequests;

    private ArrayList<String> mFriendRequestSenderNames;

    private ListView mFriendRequestView;

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

        // UI elements
        mFriendRequestView = (ListView) view.findViewById(R.id.friend_request_listView);

        // Get friends name
        mFriendRequestSenderNames = new ArrayList<>();
        for (FriendRequest request: mFriendRequests){
            mFriendRequestSenderNames.add(request.getSender().getName() + " " + request.getSender().getSurname());
        }

        FriendRequestsArrayAdapter adapter = new FriendRequestsArrayAdapter(view.getContext(), mFriendRequestSenderNames);
        mFriendRequestView.setAdapter(adapter);

        return view;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_element_received_friend_request, parent, false);
            }

            // Populate the data
            TextView senderNameView = (TextView) convertView.findViewById(R.id.request_sender_name);
            senderNameView.setText(names.get(position));

            // TODO button adapter.

            return convertView;

        }
    }
}
