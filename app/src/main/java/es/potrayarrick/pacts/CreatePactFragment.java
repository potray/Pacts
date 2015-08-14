package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCreatePactInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreatePactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePactFragment extends Fragment {
    private static final String TAG = "CreatePactFrag";

    public static final String ARG_SENDER_EMAIL = "sender email";
    public static final String ARG_RECEIVER_EMAIL = "receiver email";
    public static final String ARG_PACT_TYPES = "pact types";

    private String mSenderEmail;
    private String mReceiverEmail;
    private ArrayList<String> mPactTypes;

    private Spinner mPactTypesSpinner;

    private String type;

    private CreatePactTypeDialogFragment mCreatePactTypeDialogFragment;

    private OnCreatePactInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param senderEmail the email of the pact request sender.
     * @param receiverEmail the email of the receiver.
     * @param pactTypes the pact types the user has created.
     * @return A new instance of fragment CreatePactFragment.
     */
    public static CreatePactFragment newInstance(String senderEmail, String receiverEmail, ArrayList<String> pactTypes) {
        CreatePactFragment fragment = new CreatePactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SENDER_EMAIL, senderEmail);
        args.putString(ARG_RECEIVER_EMAIL, receiverEmail);
        args.putSerializable(ARG_PACT_TYPES, pactTypes);
        fragment.setArguments(args);
        return fragment;
    }

    public CreatePactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreatePactTypeDialogFragment = new CreatePactTypeDialogFragment();
        if (getArguments() != null) {
            mSenderEmail = getArguments().getString(ARG_SENDER_EMAIL);
            mReceiverEmail = getArguments().getString(ARG_RECEIVER_EMAIL);
            mPactTypes = new ArrayList<>();

            ArrayList<String> argTypes = (ArrayList<String>)getArguments().getSerializable(ARG_PACT_TYPES);
            if (argTypes != null){
                Log.d(TAG, "onCreate");
                // Add the types with a good format.
                for (String type: argTypes) {
                    mPactTypes.add(formatType(type));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_pact, container, false);

        // UI elements
        EditText pactName = (EditText) view.findViewById(R.id.pact_name);
        EditText pactDescription = (EditText) view.findViewById(R.id.pact_description);
        CheckBox isPromiseCheckbox = (CheckBox) view.findViewById(R.id.pact_is_promise);
        mPactTypesSpinner= (Spinner) view.findViewById(R.id.pact_type);

        String name;
        String description;
        boolean isPromise;

        // Populate the spinner
        mPactTypes.add(getString(R.string.create_pact_type));

        setSpinnerAdapter();


        mPactTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected - mPactTypes.size = " + mPactTypes.size());
                if (position == mPactTypes.size() - 1 || mPactTypes.size() == 1) {
                    mCreatePactTypeDialogFragment.show(getFragmentManager(), null);
                } else {
                    type = mPactTypes.get(position);
                    Log.d(TAG, "onItemClick - type = " + type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void setSpinnerAdapter() {
        // Create an ArrayAdapter using the default layout and mPactTypes.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mPactTypes);
        // Specify the layout to use when showing the list.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPactTypesSpinner.setAdapter(adapter);
    }


    private String formatType (String type) {
        StringBuilder formattedType = new StringBuilder(type);
        formattedType.setCharAt(0, Character.toUpperCase(formattedType.charAt(0)));
        return formattedType.toString();

    }

    public void addPactType(String newType){
        String formattedType = formatType(newType);

        if (!mPactTypes.contains(formattedType)) {
            mPactTypes.add(mPactTypes.size() - 1, formattedType);
            // We need to recreate the ArrayAdapter so we can click on the new pact type.
            Log.d(TAG, "addPactType");
            setSpinnerAdapter();

            ArrayList<String> newArg = new ArrayList<>(mPactTypes);
            newArg.remove(newArg.size() - 1);

            // We need to set again the pact types argument for this fragment, without the "create type".
            getArguments().putSerializable(ARG_PACT_TYPES, newArg);
        }
    }


    public void onCreatePactPressed() {
        if (mListener != null) {
            mListener.onCreatePactPressed();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCreatePactInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCreatePactInteractionListener");
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
    public interface OnCreatePactInteractionListener {
        public void onCreatePactPressed();
    }

}
