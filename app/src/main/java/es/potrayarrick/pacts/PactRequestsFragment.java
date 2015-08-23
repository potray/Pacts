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
    /**
     * Debug tag.
     */
    private static final String TAG = "PactRequestsFragment";
    /**
     * An argument name for {@link #newInstance(ArrayList)} <code>pactRequests</code> parameter.
     */
    public static final String ARG_PACT_REQUESTS = "pact requests";

    /**
     * The pact requests.
     */
    private ArrayList<PactRequest> mPactRequests;

    /**
     * An instance of {@link es.potrayarrick.pacts.PactRequestsFragment.OnPactRequestsInteractionListener}.
     */
    private OnPactRequestsInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pactRequests A list of pact requests.
     * @return A new instance of fragment PactRequestsFragment.
     */
    @SuppressWarnings("unused")
    public static PactRequestsFragment newInstance(final ArrayList<PactRequest> pactRequests) {
        PactRequestsFragment fragment = new PactRequestsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PACT_REQUESTS, pactRequests);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public PactRequestsFragment() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "onCreate - " + getArguments().toString());
            mPactRequests = (ArrayList<PactRequest>) getArguments().getSerializable(ARG_PACT_REQUESTS);
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
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

        if (mPactRequests != null) {
            for (PactRequest request : mPactRequests) {

                PactRequestItem item = new PactRequestItem();
                Pact pact = request.getPact();
                item.pactName = pact.getName();

                String email = pact.getUser1Email();
                if (email.equals(((MainActivity) getActivity()).getUserEmail())) {
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
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                mListener.onPactRequestItemPressed(mPactRequests.get(position).getPact());
            }
        });


        return view;
    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPactRequestsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPactRequestsInteractionListener");
        }
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Deletes a pact request from the list.
     *
     * @param pact the pact.
     * @return true if {@link #mPactRequests} is empty, so {@link PactsFragment} can hide the
     * notification menu.
     * @see PactsFragment#setHidePactRequestButton(boolean)
     */
    public final boolean deletePactRequest(final Pact pact) {
        for (PactRequest pactRequest : mPactRequests) {
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
        /**
         * Triggered when a pact request is pressed on the list.
         * @param pact the pact.
         */
        void onPactRequestItemPressed(Pact pact);
    }

    /**
     * A class that wraps a list item contents. It provides usability and extension for later use.
     */
    private class PactRequestItem {
        /**
         * The pact name.
         */
        private String pactName;
        /**
         * The name and surname of the other user of the pact.
         */
        private String userName;
    }

    /**
     * An adapter for the pact request list.
     */
    private class PactRequestsAdapter extends BaseAdapter {

        /**
         * The context.
         */
        private Context context;
        /**
         * A list of pact requests.
         */
        private ArrayList<PactRequestItem> items;

        /**
         * The constructor.
         * @param context the context.
         * @param items a list of pact requests.
         */
        public PactRequestsAdapter(final Context context, final ArrayList<PactRequestItem> items) {
            this.context = context;
            this.items = new ArrayList<>(items);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
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
        public Object getItem(final int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }
    }

}
