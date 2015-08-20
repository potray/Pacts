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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import backend.pacts.potrayarrick.es.pacts.model.Pact;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPactsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PactsFragment extends Fragment {
    private static final String TAG = "PactsFragment";

    public static final String ARG_PACTS = "pacts";
    public static final String ARG_HIDE_PACT_REQUESTS_MENU = "hide pact requests menu";

    private ArrayList<Pact> mPacts;
    private boolean mHidePactRequestButton;

    private OnPactsFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pacts the user's pacts.
     * @param hidePactRequestButton whether to hide the pact requests button.
     * @return A new instance of fragment PactsFragment.
     */
    @SuppressWarnings("unused")
    public static PactsFragment newInstance(ArrayList<Pact> pacts, boolean hidePactRequestButton) {
        PactsFragment fragment = new PactsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PACTS, pacts);
        args.putBoolean(ARG_HIDE_PACT_REQUESTS_MENU, hidePactRequestButton);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor
     */
    public PactsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPacts = new ArrayList<>();
        if (getArguments() != null) {
            mPacts = (ArrayList<Pact>) getArguments().getSerializable(ARG_PACTS);
            Log.d(TAG, "onCreate - " + getArguments().toString());
            mHidePactRequestButton = getArguments().getBoolean(ARG_HIDE_PACT_REQUESTS_MENU);
        } else {
            Log.d(TAG, "onCreate - No arguments!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // We have a menu in this fragment.
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pacts, container, false);

        // Fill the content of the list's items (if any).
        ArrayList<PactItem> pactItems = new ArrayList<>();
        if (mPacts != null) {
            for (Pact pact : mPacts){
                PactItem item = new PactItem();

                // Get the email of the other user of the pact.
                String user1Email = pact.getUser1Email();
                if (user1Email.equals(((MainActivity)getActivity()).getUserEmail())){
                    item.userName = pact.getUser2CompleteName();
                } else {
                    item.userName = pact.getUser1CompleteName();
                }

                item.pactName = pact.getName();

                pactItems.add(item);
            }

            // Set the adapter for the list
            ListView pactListView = (ListView) view.findViewById(R.id.pacts_list);
            PactsAdapter adapter = new PactsAdapter(view.getContext(), pactItems);
            pactListView.setAdapter(adapter);

            // TODO addListener.
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear previous menu
        menu.clear();
        // Swap menu bars.
        inflater.inflate(R.menu.menu_pacts, menu);

        // Hide notification button if necessary.
        if (mHidePactRequestButton){
            Log.d(TAG, "onCreateOptionsMenu - Hide pact request button");
            menu.findItem(R.id.menu_pacts_requests).setVisible(false);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPactsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFriendsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_pacts_requests){
            mListener.onMenuPactsRequests();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setHidePactRequestButton (boolean hide){
        Log.d(TAG, "setHidePactRequestButton " + hide);

        // Update the argument of the fragment.
        getArguments().putBoolean(ARG_HIDE_PACT_REQUESTS_MENU, hide);

        mHidePactRequestButton = hide;
    }

    public void addPact (Pact pact){
        if (mPacts != null) {
            // The fragment could be created and we need to update it.
            mPacts.add(pact);
        } else {
            mPacts = new ArrayList<>();
            mPacts.add(pact);
        }

        // Update the argument.
        getArguments().putSerializable(ARG_PACTS, mPacts);
    }

    /**
     * An interface to communicate with {@link MainActivity}.
     */
    public interface OnPactsFragmentInteractionListener {
        void onMenuPactsRequests();
    }

    /**
     * A class that wraps a list item contents. It provides usability and extension for later use.
     */
    private class PactItem {
        // I make them protected so I don't have to access them with methods.
        protected String pactName;
        protected String userName;
    }

    private class PactsAdapter extends BaseAdapter {

        Context context;
        ArrayList<PactItem> items;

        public PactsAdapter(Context context, ArrayList<PactItem> items) {
            this.context = context;
            this.items = new ArrayList<>(items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                // Inflate the layout.
                v = LayoutInflater.from(context).inflate(R.layout.list_element_pact, parent, false);
            }

            PactItem item = items.get(position);

            if (item != null) {
                // Fill the layout with data.
                TextView pactName = (TextView) v.findViewById(R.id.pact_name);
                TextView pactUser = (TextView) v.findViewById(R.id.pact_user);

                pactName.setText(item.pactName);
                pactUser.setText(item.userName);
            }
            return v;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
