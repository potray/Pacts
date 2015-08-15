package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import backend.pacts.potrayarrick.es.pacts.Pacts;


/**
 * A fragment for creating a pact.
 */
public class CreatePactFragment extends Fragment {
    /**
     * Debug tag.
     */
    private static final String TAG = "CreatePactFrag";


    /**
     * An argument name for {@link #newInstance(String, String, ArrayList)} <code>senderEmail</code> parameter.
     */
    public static final String ARG_SENDER_EMAIL = "sender email";
    public static final String ARG_RECEIVER_EMAIL = "receiver email";
    public static final String ARG_PACT_TYPES = "pact types";

    private String mSenderEmail;
    private String mReceiverEmail;
    private ArrayList<String> mPactTypes;

    private Spinner mPactTypesSpinner;

    private String mPactType;

    private CreatePactTypeDialogFragment mCreatePactTypeDialogFragment;

    private Pacts mPactsService;

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
        mPactsService = null;
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
        final EditText pactName = (EditText) view.findViewById(R.id.pact_name);
        final EditText pactDescription = (EditText) view.findViewById(R.id.pact_description);
        final TextView pactTypeHint = (TextView) view.findViewById(R.id.pact_type_hint);
        final CheckBox isPromiseCheckbox = (CheckBox) view.findViewById(R.id.pact_is_promise);
        Button createPactButton = (Button) view.findViewById(R.id.create_pact_button);
        Button createPactTypeButton = (Button) view.findViewById(R.id.create_pact_type_button);
        mPactTypesSpinner= (Spinner) view.findViewById(R.id.pact_type);

        // The AsyncTask
        final CreatePactAsyncTask createPactTask = new CreatePactAsyncTask(getActivity().getApplicationContext());

        // Populate the spinner
        setSpinnerAdapter();

        mPactTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPactType = mPactTypes.get(position);
                Log.d(TAG, "onItemClick - type = " + mPactType);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected ");
            }
        });

        // Create pact type callback
        createPactTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreatePactTypeDialogFragment.show(getFragmentManager(), null);
            }
        });


        // Set the checkbox so it hides the pact type when selected.
        isPromiseCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPromiseCheckbox.isChecked()){
                    pactTypeHint.setVisibility(View.INVISIBLE);
                    mPactTypesSpinner.setVisibility(View.INVISIBLE);
                } else {
                    pactTypeHint.setVisibility(View.VISIBLE);
                    mPactTypesSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        // Create pact button callback
        createPactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO check valid inputs.
                // Get values from input text and checkbox.

                String name, description;
                boolean isPromise;

                name = pactName.getText().toString();
                description = pactDescription.getText().toString();
                isPromise = isPromiseCheckbox.isChecked();

                // Launch the task.
                ArrayList<String> taskStringArguments = new ArrayList<>();
                taskStringArguments.add(mSenderEmail);
                taskStringArguments.add(mReceiverEmail);
                taskStringArguments.add(name);
                taskStringArguments.add(description);
                taskStringArguments.add(mPactType);
                createPactTask.execute(new Pair<>(taskStringArguments, isPromise));
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
            mPactTypes.add(formattedType);
            // We need to recreate the ArrayAdapter so we can click on the new pact type.
            Log.d(TAG, "addPactType");
            setSpinnerAdapter();
            ArrayList<String> newArg = new ArrayList<>(mPactTypes);

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

    private class CreatePactAsyncTask extends AsyncTask<Pair<ArrayList<String>, Boolean>, Void, Void>{

        private Context context;

        protected CreatePactAsyncTask (Context context){
            this.context = context;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(Pair<ArrayList<String>, Boolean>... params) {
            // Set up a pacts service.
            mPactsService = Utils.setUpPactsService();

            ArrayList<String> stringArgs = params[0].first;
            Log.d(TAG, "doInBackground " + stringArgs.toString());


            // We send all the data, the backend will know what to do with them.
            try {
                mPactsService.sendPactRequest(stringArgs.get(0), stringArgs.get(1), stringArgs.get(2),
                        stringArgs.get(3), stringArgs.get(4), params[0].second).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Delete the service
            mPactsService = null;

            Toast toast = Toast.makeText(context, getString(R.string.info_pact_created), Toast.LENGTH_SHORT);
            toast.show();

            // TODO tell mainActivity to "press back"
        }
    }

}
