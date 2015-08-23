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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import backend.pacts.potrayarrick.es.pacts.model.Pact;

/**
 * The pacts fragment.
 */
public class PactsFragment extends Fragment {
    /**
     * Debug tag.
     */
    private static final String TAG = "PactsFragment";

    /**
     * An argument name for {@link #newInstance(ArrayList, boolean)} <code>pacts</code> parameter.
     */
    public static final String ARG_PACTS = "pacts";
    /**
     * An argument name for {@link #newInstance(ArrayList, boolean)} <code>hidePactRequestButton</code> parameter.
     */
    public static final String ARG_HIDE_PACT_REQUESTS_MENU = "hide pact requests menu";

    /**
     * The pacts.
     */
    private ArrayList<Pact> mPacts;
    /**
     * Whether or not the pact requests menu button should be hidden.
     */
    private boolean mHidePactRequestButton;

    /**
     * An instance of {@link es.potrayarrick.pacts.PactsFragment.OnPactsFragmentInteractionListener}.
     */
    private OnPactsFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pacts                 the user's pacts.
     * @param hidePactRequestButton whether to hide the pact requests button.
     * @return A new instance of fragment PactsFragment.
     */
    @SuppressWarnings("unused")
    public static PactsFragment newInstance(final ArrayList<Pact> pacts, final boolean hidePactRequestButton) {
        PactsFragment fragment = new PactsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PACTS, pacts);
        args.putBoolean(ARG_HIDE_PACT_REQUESTS_MENU, hidePactRequestButton);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public PactsFragment() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onCreate(final Bundle savedInstanceState) {
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
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        // We have a menu in this fragment.
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pacts, container, false);

        // Fill the content of the list's items (if any).
        ArrayList<PactItem> pactItems = new ArrayList<>();
        if (mPacts != null) {
            for (Pact pact : mPacts) {
                PactItem item = new PactItem();

                // Get the email of the other user of the pact.
                String user1Email = pact.getUser1Email();
                if (user1Email.equals(((MainActivity) getActivity()).getUserEmail())) {
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

            pactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                    mListener.onPactPressed(mPacts.get(position));
                }
            });
        }
        return view;
    }

    @Override
    public final void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear previous menu
        menu.clear();
        // Swap menu bars.
        inflater.inflate(R.menu.menu_pacts, menu);

        // Hide notification button if necessary.
        if (mHidePactRequestButton) {
            Log.d(TAG, "onCreateOptionsMenu - Hide pact request button");
            menu.findItem(R.id.menu_pacts_requests).setVisible(false);
        }

    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPactsFragmentInteractionListener) activity;
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

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.menu_pacts_requests) {
            mListener.onMenuPactsRequests();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets {@link #mHidePactRequestButton}.
     * @param hide the new {@link #mHidePactRequestButton}.
     */
    public final void setHidePactRequestButton(final boolean hide) {
        Log.d(TAG, "setHidePactRequestButton " + hide);

        // Update the argument of the fragment.
        getArguments().putBoolean(ARG_HIDE_PACT_REQUESTS_MENU, hide);

        mHidePactRequestButton = hide;
    }

    /**
     * Adds a pact to {@link #mPacts}.
     * @param pact the pact to add.
     */
    public final void addPact(final Pact pact) {
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
        /**
         * Triggers when the pact requests menu button is pressed.
         */
        void onMenuPactsRequests();

        /**
         * Triggers when a pact is pressed.
         * @param pact the pressed pact.
         */
        void onPactPressed(Pact pact);
    }

    /**
     * A class that wraps a list item contents. It provides usability and extension for later use.
     */
    private class PactItem {
        /**
         * The name of the pact.
         */
        private String pactName;
        /**
         * The name and surname of the other pact's other user.
         */
        private String userName;
    }

    /**
     * An adapter for the pact list.
     */
    private class PactsAdapter extends BaseAdapter {

        /**
         * The context.
         */
        private Context context;
        /**
         * A list of pacts.
         */
        private ArrayList<PactItem> items;

        /**
         * The constructor.
         * @param context the context.
         * @param items the items of the list.
         */
        public PactsAdapter(final Context context, final ArrayList<PactItem> items) {
            this.context = context;
            this.items = new ArrayList<>(items);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
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
        public Object getItem(final int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }
    }
}
