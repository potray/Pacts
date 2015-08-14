package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import backend.pacts.potrayarrick.es.pacts.Pacts;

/**
 * A dialog with an email input and a find button.
 */
public class CreatePactTypeDialogFragment extends DialogFragment {

    /**
     * The type input text.
     */
    private EditText mTypeEditText;
    /**
     * The pacts service.
     */
    private static Pacts mPactsService = null;
    /**
     * The asynchronous task.
     */
    private CreatePactTypeTask mCreatePactTypeTask = null;

    /**
     * A listener to communicate with {@link MainActivity}.
     */
    private OnCreatePactTypeDialogFragmentInteractionListener mListener;

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_create_pact_type, container);

        mTypeEditText = (EditText) view.findViewById(R.id.pact_type_input);
        Button createButton = (Button) view.findViewById(R.id.create_pact_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String type = mTypeEditText.getText().toString();

                // TODO check the type doesn't exists.

                // Get the user email from shared preferences.
                SharedPreferences preferences = getActivity().getSharedPreferences(Utils.PREFS_NAME, 0);
                String email = preferences.getString(Utils.Strings.USER_EMAIL, "");

                // Launch task
                mCreatePactTypeTask = new CreatePactTypeTask(email, type, getActivity().getApplicationContext());
                mCreatePactTypeTask.execute();
            }
        });

        mTypeEditText.requestFocus();
        getDialog().setTitle(R.string.create_pact_type);

        return view;
    }

    private void sendNewPactTypeToPactFragment(String newType){
        if (mListener != null) {
            mListener.onCreatePactType(newType);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCreatePactTypeDialogFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCreatePactTypeDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * An asynchronous task for sending a friend request to backend.
     */
    private class CreatePactTypeTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * The user email.
         */
        private String email;

        /**
         * The type to create.
         */
        private String type;

        /**
         * The context, needed for toasts.
         */
        private Context context;

        /**
         * Default constructor.
         * @param email the user email.
         * @param type the type to create.
         * @param context the context of the activity.
         */
        protected CreatePactTypeTask(final String email, final String type, final Context context) {
            this.email = email;
            this.type = type;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            // Get the service
            if (mPactsService == null) {
                Pacts.Builder builder;
                if (Utils.LOCAL_TESTING) {
                    builder = new Pacts.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            // - 10.0.2.2 is localhost's IP address in Android emulator
                            .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                            .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                @Override
                                public void initialize(final AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                    abstractGoogleClientRequest.setDisableGZipContent(true);
                                }
                            });
                } else {
                    builder = new Pacts.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null).setRootUrl("https://pacts-1027.appspot.com/_ah/api/");
                }
                mPactsService = builder.build();
            }

            // Send the request
            try {
                mPactsService.createPactType(email, type).execute();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreatePactTypeTask = null;
            if (success){
                /*Toast toast = Toast.makeText(context, getString(R.string.info_pact_type_created), Toast.LENGTH_SHORT);
                toast.show();*/
                sendNewPactTypeToPactFragment(type.toLowerCase());
                // Clean the text since if the user creates another type the current one will be there.
                mTypeEditText.setText("");
                dismiss();
            }
        }
    }


    public interface OnCreatePactTypeDialogFragmentInteractionListener {
        void onCreatePactType(String type);
    }
}
