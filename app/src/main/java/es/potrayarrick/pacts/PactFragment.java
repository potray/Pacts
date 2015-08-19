package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import backend.pacts.potrayarrick.es.pacts.model.Pact;


/**
 * The pact fragment.
 */
public class PactFragment extends Fragment {
    public static final String ARG_PACT = "pact";
    private static final String TAG = "PactFragment";

    private Pact mPact;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pact The pact.
     * @return A new instance of fragment PactFragment.
     */
    public static PactFragment newInstance(Pact pact) {
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
    public PactFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Pact> pactArgument = (ArrayList<Pact>) getArguments().getSerializable(ARG_PACT);
            if (pactArgument != null) {
                Log.d(TAG, "onCreate - pactArgument = " + pactArgument.toString());
                mPact = pactArgument.get(0);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pact, container, false);

        // UI elements
        TextView pactTypeAndUserView = (TextView) view.findViewById(R.id.pact_type_and_user);
        TextView pactDescriptionView = (TextView) view.findViewById(R.id.pact_description);
        TextView pactCreationDateView = (TextView) view.findViewById(R.id.pact_date);

        // Get the type and user string.
        String userName;
        String pactTypeIntro = getString(R.string.pact_type_and_user_intro);
        String pactTypeMiddle = getString(R.string.pact_type_and_user_middle);

        if (((MainActivity)getActivity()).getUserEmail().equals(mPact.getUser1Email())){
            userName = mPact.getUser2CompleteName();
        } else {
            userName = mPact.getUser1CompleteName();
        }

        String typeAndUser = pactTypeIntro + " " + mPact.getType() + " " +
                pactTypeMiddle + " " + userName;

        pactTypeAndUserView.setText(typeAndUser);
        pactDescriptionView.setText(mPact.getDescription());

        // Set the creation date string.
        String creationDateStringStart = getString(R.string.pact_info_creation_date);
        Locale deviceLanguage = getResources().getConfiguration().locale;
        // We need to create a Java date, since getCreationDate() returns com.google.api.client.util.DateTime.
        Date creationDate = new Date(mPact.getCreationDate().getValue());
        String creationDateString;

        // Spanish is not standard so we have to create it.
        Locale spanishLocale = new Locale ("es", "ES");

        if (deviceLanguage.equals(spanishLocale)){
            creationDateString = new SimpleDateFormat("dd/MM/yyyy", spanishLocale).format(creationDate);
        } else {
            // English by default.
            creationDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(creationDate);
        }

        pactCreationDateView.setText(creationDateStringStart + " " + creationDateString);

        // Hide the "not accepted" if necessary.
        if (mPact.getAccepted()){
            view.findViewById(R.id.pact_not_accepted_layout).setVisibility(View.INVISIBLE);
        } else {
            // Set the accept pact callback.
            Button acceptPactButton = (Button) view.findViewById(R.id.accept_pact_button);

            acceptPactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    /**
     * An
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
