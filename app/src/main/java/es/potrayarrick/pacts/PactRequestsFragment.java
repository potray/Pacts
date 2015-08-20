package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import backend.pacts.potrayarrick.es.pacts.model.Pact;
import backend.pacts.potrayarrick.es.pacts.model.PactRequest;


/**
 * The pact requests fragment.
 */
public class PactRequestsFragment extends Fragment {
    public static final String ARG_PACT_REQUESTS = "pact requests";
    private static final String TAG = "PactRequestsFragment";

    private ArrayList<PactRequest> mPactRequests;

    private OnPactRequestsInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pactRequests A list of pact requests.
     * @return A new instance of fragment PactRequestsFragment.
     */
    @SuppressWarnings("unused")
    public static PactRequestsFragment newInstance(ArrayList<PactRequest> pactRequests) {
        PactRequestsFragment fragment = new PactRequestsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PACT_REQUESTS, pactRequests);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public PactRequestsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "onCreate - " + getArguments().toString());
            mPactRequests = (ArrayList<PactRequest>) getArguments().getSerializable(ARG_PACT_REQUESTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pact_requests, container, false);

        // Add back button to the action bar
        setHasOptionsMenu(true);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            setHasOptionsMenu(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        // Fill the contents of the listView.
        ArrayList<PactRequestItem> listItems = new ArrayList<>();
        ListView listView = (ListView) view.findViewById(R.id.pact_requests_list);

        if (mPactRequests != null){
            for (PactRequest request: mPactRequests){

                PactRequestItem item = new PactRequestItem();
                Pact pact = request.getPact();
                item.pactName = pact.getName();

                String email = pact.getUser1Email();
                if (email.equals(((MainActivity)getActivity()).getUserEmail())){
                    item.userName = pact.getUser2CompleteName();
                } else {
                    item.userName = pact.getUser1CompleteName();
                }

                listItems.add(item);
            }
        }

        // Set the adapter of the list.
        PactRequestsAdapter adapter = new PactRequestsAdapter(view.getContext(), listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onPactRequestItemPressed(mPactRequests.get(position).getPact());
            }
        });


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPactRequestsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPactRequestsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
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
     * Deletes a pact request from the list.
     * @param pact the pact.
     * @return true if {@link #mPactRequests} is empty, so {@link PactsFragment} can hide the
     * notification menu.
     * @see PactsFragment#setHidePactRequestButton(boolean)
     */
    public boolean deletePactRequest(Pact pact){
        for (PactRequest pactRequest : mPactRequests){
            if (pactRequest.getPact().getId().equals(pact.getId())) {
                mPactRequests.remove(pactRequest);
            }
        }

        return mPactRequests.isEmpty();
    }

    /**
     * An interface to communicate with {@link MainActivity}.
     */
    public interface OnPactRequestsInteractionListener {
        void onPactRequestItemPressed(Pact pact);
    }

    /**
     * A class that wraps a list item contents. It provides usability and extension for later use.
     */
    private class PactRequestItem {
        // I make them protected so I don't have to access them with methods.
        protected String pactName;
        protected String userName;
    }

    private class PactRequestsAdapter extends BaseAdapter {

        Context context;
        ArrayList<PactRequestItem> items;

        public PactRequestsAdapter(Context context, ArrayList<PactRequestItem> items) {
            this.context = context;
            this.items = new ArrayList<>(items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                // Inflate the layout.
                // TODO this is temporal. Replace with the real list item.
                v = LayoutInflater.from(context).inflate(R.layout.list_element_pact, parent, false);
            }

            PactRequestItem item = items.get(position);

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
