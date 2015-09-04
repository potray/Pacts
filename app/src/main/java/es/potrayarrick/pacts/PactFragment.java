package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import backend.pacts.potrayarrick.es.pacts.model.Pact;


/**
 * The pact fragment.
 */
public class PactFragment extends Fragment {
    /**
     * Debug tag.
     */
    private static final String TAG = "PactFragment";
    /**
     * An argument name for {@link #newInstance(Pact)} <code>pact</code> parameter.
     */
    public static final String ARG_PACT = "pact";

    /**
     * The pact.
     */
    private Pact mPact;

    /**
     * The instance of {@link es.potrayarrick.pacts.PactFragment.OnPactFragmentInteractionListener}.
     */
    private OnPactFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pact The pact.
     * @return A new instance of fragment PactFragment.
     */
    @SuppressWarnings("unused")
    public static PactFragment newInstance(final Pact pact) {
        PactFragment fragment = new PactFragment();
        Bundle args = new Bundle();
        // We use an arraylist since it's automatically serialized.
        ArrayList<Pact> pactArgument = new ArrayList<>();
        pactArgument.add(pact);
        args.putSerializable(ARG_PACT, pactArgument);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public PactFragment() {
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            @SuppressWarnings("unchecked")
            ArrayList<Pact> pactArgument = (ArrayList<Pact>) getArguments().getSerializable(ARG_PACT);
            if (pactArgument != null) {
                Log.d(TAG, "onCreate - pactArgument = " + pactArgument.toString());
                mPact = pactArgument.get(0);
            }
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pact, container, false);

        // Add back button to the action bar
        setHasOptionsMenu(true);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            setHasOptionsMenu(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        // UI elements
        TextView pactTypeAndUserView = (TextView) view.findViewById(R.id.pact_type_and_user);
        TextView pactDescriptionView = (TextView) view.findViewById(R.id.pact_description);
        TextView pactCreationDateView = (TextView) view.findViewById(R.id.pact_date);

        // Get the type and user string.
        String userName;
        String pactTypeIntro = getString(R.string.pact_type_and_user_intro);
        String pactTypeMiddle = getString(R.string.pact_type_and_user_middle);
        String pactTypePromise = getString(R.string.pact_type_promise);

        if (((MainActivity) getActivity()).getUserEmail().equals(mPact.getUser1Email())) {
            userName = mPact.getUser2CompleteName();
        } else {
            userName = mPact.getUser1CompleteName();
        }

        String typeAndUser;

        if (mPact.getPromise()){
            typeAndUser = pactTypePromise + " " + userName;
        } else {
            typeAndUser = pactTypeIntro + " " + mPact.getType() + " "
                    + pactTypeMiddle + " " + userName;
        }


        pactTypeAndUserView.setText(typeAndUser);
        pactDescriptionView.setText(mPact.getDescription());

        // Set the creation date string.
        String creationDateStringStart = getString(R.string.pact_info_creation_date);
        Locale deviceLanguage = getResources().getConfiguration().locale;
        // We need to create a Java date, since getCreationDate() returns com.google.api.client.util.DateTime.
        Date creationDate = new Date(mPact.getCreationDate().getValue());
        String creationDateString;

        // Spanish is not standard so we have to create it.
        Locale spanishLocale = new Locale("es", "ES");

        if (deviceLanguage.equals(spanishLocale)) {
            creationDateString = new SimpleDateFormat("dd/MM/yyyy", spanishLocale).format(creationDate);
        } else {
            // English by default.
            creationDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(creationDate);
        }

        pactCreationDateView.setText(creationDateStringStart + " " + creationDateString);


        Button rejectPactButton = (Button) view.findViewById(R.id.reject_pact_button);
        Button acceptPactButton = (Button) view.findViewById(R.id.accept_pact_button);

        // If the pact's user1's email is the same as the user, hide the accept and reject buttons.
        if (mPact.getUser1Email().equals(((MainActivity)getActivity()).getUserEmail())){
            rejectPactButton.setVisibility(View.INVISIBLE);
            acceptPactButton.setVisibility(View.INVISIBLE);
        }

        // Hide the "not accepted" if necessary.
        if (mPact.getAccepted()) {
            view.findViewById(R.id.pact_not_accepted_layout).setVisibility(View.INVISIBLE);
        } else {
            // Set callbacks
            acceptPactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mListener.onAcceptPact(mPact);
                }
            });

            rejectPactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mListener.onRejectPact(mPact);
                }
            });
        }

        return view;
    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPactFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPactFragmentInteractionListener");
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
     * When a pact request is answered {@link MainActivity} tells this fragment the backend did it's work.
     *
     * @param accepted whether or not the pact request was accepted.
     */
    public final void pactRequestAnswered(final boolean accepted) {
        // Hide the "pact not accepted" section.
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.pact_not_accepted_layout).setVisibility(View.INVISIBLE);

            // Show a toast.
            Toast toast;

            if (accepted) {
                toast = Toast.makeText(view.getContext(), R.string.info_pact_accepted, Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(view.getContext(), R.string.info_pact_rejected, Toast.LENGTH_SHORT);
            }

            toast.show();
        }
    }

    /**
     * An interface to communicate with {@link MainActivity}.
     */
    public interface OnPactFragmentInteractionListener {
        /**
         * Triggered when the pact is accepted.
         *
         * @param pact this pact.
         */
        void onAcceptPact(Pact pact);

        /**
         * Triggered when the pact is rejected.
         *
         * @param pact this pact.
         */
        void onRejectPact(Pact pact);
    }
}
