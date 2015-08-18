package es.potrayarrick.pacts;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

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

        // Hide the "not accepted" if necessary.
        if (mPact.getAccepted()){
            view.findViewById(R.id.unaccepted_pact_text).setVisibility(View.INVISIBLE);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
