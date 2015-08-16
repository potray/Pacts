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
import android.view.inputmethod.InputMethodManager;
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
    /**
     * An argument name for {@link #newInstance(String, String, ArrayList)} <code>receiverEmail</code> parameter.
     */
    public static final String ARG_RECEIVER_EMAIL = "receiver email";
    /**
     * An argument name for {@link #newInstance(String, String, ArrayList)} <code>pactTypes</code> parameter.
     */
    public static final String ARG_PACT_TYPES = "pact types";

    /**
     * The pact request sender email.
     */
    private String mSenderEmail;
    /**
     * The pact request sender receiver.
     */
    private String mReceiverEmail;
    /**
     * A list with the user's created pact types.
     */
    private ArrayList<String> mPactTypes;

    /**
     * The spinner with the pact types.
     */
    private Spinner mPactTypesSpinner;

    /**
     * The pact type selected.
     */
    private String mPactType;

    /**
     * The create pact type dialog.
     */
    private CreatePactTypeDialogFragment mCreatePactTypeDialogFragment;

    /**
     * The Pacts service.
     */
    private Pacts mPactsService;

    /**
     * The fragment's interface to communicate with {@link MainActivity}.
     */
    private OnCreatePactInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param senderEmail   the email of the pact request sender.
     * @param receiverEmail the email of the receiver.
     * @param pactTypes     the pact types the user has created.
     * @return A new instance of fragment CreatePactFragment.
     */
    @SuppressWarnings("unused")
    public static CreatePactFragment newInstance(final String senderEmail, final String receiverEmail, final ArrayList<String> pactTypes) {
        CreatePactFragment fragment = new CreatePactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SENDER_EMAIL, senderEmail);
        args.putString(ARG_RECEIVER_EMAIL, receiverEmail);
        args.putSerializable(ARG_PACT_TYPES, pactTypes);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public CreatePactFragment() {
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPactsService = null;
        mCreatePactTypeDialogFragment = new CreatePactTypeDialogFragment();
        if (getArguments() != null) {
            mSenderEmail = getArguments().getString(ARG_SENDER_EMAIL);
            mReceiverEmail = getArguments().getString(ARG_RECEIVER_EMAIL);
            mPactTypes = new ArrayList<>();

            ArrayList<String> argTypes = (ArrayList<String>) getArguments().getSerializable(ARG_PACT_TYPES);
            if (argTypes != null) {
                Log.d(TAG, "onCreate");
                // Add the types with a good format.
                for (String type : argTypes) {
                    mPactTypes.add(formatType(type));
                }
            }
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_pact, container, false);

        // UI elements
        final EditText pactName = (EditText) view.findViewById(R.id.pact_name);
        final EditText pactDescription = (EditText) view.findViewById(R.id.pact_description);
        final TextView pactTypeHint = (TextView) view.findViewById(R.id.pact_type_hint);
        final CheckBox isPromiseCheckbox = (CheckBox) view.findViewById(R.id.pact_is_promise);
        Button createPactButton = (Button) view.findViewById(R.id.create_pact_button);
        Button createPactTypeButton = (Button) view.findViewById(R.id.create_pact_type_button);
        mPactTypesSpinner = (Spinner) view.findViewById(R.id.pact_type);

        // The AsyncTask
        final CreatePactAsyncTask createPactTask = new CreatePactAsyncTask(getActivity().getApplicationContext());

        // Populate the spinner
        setSpinnerAdapter();

        mPactTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                mPactType = mPactTypes.get(position);
                Log.d(TAG, "onItemClick - type = " + mPactType);

            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected ");
            }
        });

        // Create pact type callback
        createPactTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mCreatePactTypeDialogFragment.show(getFragmentManager(), null);
            }
        });


        // Set the checkbox so it hides the pact type when selected.
        isPromiseCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isPromiseCheckbox.isChecked()) {
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
            public void onClick(final View v) {
                // Get values from input text and checkbox.

                String name, description;
                boolean isPromise;

                name = pactName.getText().toString().trim();
                description = pactDescription.getText().toString().trim();
                isPromise = isPromiseCheckbox.isChecked();

                // Integrity checks.
                boolean continueTask = true;
                View focusView = null;

                if (name.isEmpty()) {
                    continueTask = false;
                    pactName.setError(getString(R.string.error_field_required));
                    focusView = pactName;
                } else if (description.isEmpty()) {
                    continueTask = false;
                    pactDescription.setError(getString(R.string.error_field_required));
                    focusView = pactDescription;
                } else if ((mPactTypes == null || mPactType.isEmpty()) && !isPromise) {
                    // There is no pact types.
                    continueTask = false;
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.error_no_pact_types, Toast.LENGTH_SHORT);
                    toast.show();
                }

                // Launch the task.
                if (continueTask) {
                    // Reset InputTexts.
                    pactName.setText("");
                    pactDescription.setText("");

                    // Hide the keyboard since it could be opened.
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getActivity().getCurrentFocus() != null) {
                        mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                    ArrayList<String> taskStringArguments = new ArrayList<>();
                    taskStringArguments.add(mSenderEmail);
                    taskStringArguments.add(mReceiverEmail);
                    taskStringArguments.add(name);
                    taskStringArguments.add(description);
                    // If it's a promise this could be null.
                    if (mPactType == null) {
                        mPactType = "none";
                    }
                    taskStringArguments.add(mPactType);
                    createPactTask.execute(new Pair<>(taskStringArguments, isPromise));
                } else if (focusView != null) {
                    focusView.requestFocus();
                }
            }
        });

        return view;
    }

    /**
     * Sets the spinner adapter to update it's contents.
     */
    private void setSpinnerAdapter() {
        // Create an ArrayAdapter using the default layout and mPactTypes.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mPactTypes);
        // Specify the layout to use when showing the list.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPactTypesSpinner.setAdapter(adapter);
    }

    /**
     * Gives a type the correct format (Blood is correct, BLOOD and blood aren't).
     *
     * @param type the type.
     * @return a String with the correct format.
     */
    private String formatType(final String type) {
        StringBuilder formattedType = new StringBuilder(type);
        formattedType.setCharAt(0, Character.toUpperCase(formattedType.charAt(0)));
        return formattedType.toString();

    }

    /**
     * Adds a new type to {@link #mPactTypesSpinner}.
     *
     * @param newType the new type to add.
     */
    public final void addPactType(final String newType) {
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

    /**
     * Tells {@link MainActivity} to go back to {@link FriendFragment} via {@link es.potrayarrick.pacts.CreatePactFragment.OnCreatePactInteractionListener}.
     */
    public final void backToFriendFragment() {
        if (mListener != null) {
            mListener.onCreatePact();
        }
    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCreatePactInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCreatePactInteractionListener");
        }
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * An interface to communicate with {@link MainActivity}.
     */
    public interface OnCreatePactInteractionListener {
        /**
         * Launches when the pact is created.
         */
        void onCreatePact();
    }

    /**
     * An asynchronous task for sending the pact info to backend.
     */
    private class CreatePactAsyncTask extends AsyncTask<Pair<ArrayList<String>, Boolean>, Void, Void> {
        /**
         * The context, for showing toasts.
         */
        private Context context;

        /**
         * Default constructor.
         *
         * @param context the context.
         */
        protected CreatePactAsyncTask(final Context context) {
            this.context = context;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(final Pair<ArrayList<String>, Boolean>... params) {
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
        protected void onPostExecute(final Void aVoid) {
            // Delete the service
            mPactsService = null;

            Toast toast = Toast.makeText(context, getString(R.string.info_pact_created), Toast.LENGTH_SHORT);
            toast.show();

            backToFriendFragment();
        }
    }

}
