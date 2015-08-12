package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
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
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            Serializable pactsArg = getArguments().getSerializable(ARG_PACTS);
            if (pactsArg != null) {
                mPacts = new ArrayList<>();
            }
            mHidePactRequestButton = getArguments().getBoolean(ARG_HIDE_PACT_REQUESTS_MENU);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // We have a menu in this fragment.
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pacts, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear previous menu
        menu.clear();
        // Swap menu bars.
        inflater.inflate(R.menu.menu_pacts, menu);
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
    public interface OnPactsFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
