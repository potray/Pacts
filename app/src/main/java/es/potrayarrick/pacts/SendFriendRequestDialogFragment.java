package es.potrayarrick.pacts;

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
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import backend.pacts.potrayarrick.es.friends.Friends;
import backend.pacts.potrayarrick.es.friends.model.Message;

/**
 * A dialog with an email input and a find button.
 */
public class SendFriendRequestDialogFragment extends DialogFragment {

    /**
     * The email input text.
     */
    private EditText mEmailEditText;
    /**
     * The friend service.
     */
    private static Friends friendsService = null;
    /**
     * The asynchronous task.
     */
    private SendFriendRequestTask mSendFriendRequestTask = null;

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_send_friend_request, container);

        mEmailEditText = (EditText) view.findViewById(R.id.email_input);
        Button mFindButton = (Button) view.findViewById(R.id.find_button);

        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email = mEmailEditText.getText().toString();

                // Get the sender email from shared preferences.
                SharedPreferences preferences = getActivity().getSharedPreferences(Utils.PREFS_NAME, 0);
                String senderEmail = preferences.getString(Utils.Strings.USER_EMAIL, "");

                if (Utils.isEmailValid(email)) {
                    if (email.equals(senderEmail)) {
                        mEmailEditText.setError(getString(R.string.error_cannot_friend_yourself));
                        mEmailEditText.requestFocus();
                    } else {
                        mSendFriendRequestTask = new SendFriendRequestTask(email, senderEmail, getActivity().getApplicationContext());
                        mSendFriendRequestTask.execute();
                    }
                } else {
                    mEmailEditText.setError(getString(R.string.error_invalid_email));
                    mEmailEditText.requestFocus();
                }
            }
        });

        // set this instance as callback for editor action
        mEmailEditText.requestFocus();
        getDialog().setTitle(R.string.title_find_user_by_email);

        return view;
    }


    /**
     * An asynchronous task for sending a friend request to backend.
     */
    private class SendFriendRequestTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * The receiver email.
         */
        private String receiverEmail;

        /**
         * The sender email.
         */
        private String senderEmail;

        /**
         * The context, needed for toasts.
         */
        private Context context;

        /**
         * Default constructor.
         * @param receiverEmail the email of the receiver.
         * @param senderEmail the email of the sender.
         * @param context the context of the activity.
         */
        protected SendFriendRequestTask(final String receiverEmail, final String senderEmail, final Context context) {
            this.receiverEmail = receiverEmail;
            this.senderEmail = senderEmail;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {

            // This is just a quick integrity check.
            if (!senderEmail.isEmpty()) {
                // Get the service
                if (friendsService == null) {
                    Friends.Builder builder;
                    if (Utils.LOCAL_TESTING) {
                        builder = new Friends.Builder(AndroidHttp.newCompatibleTransport(),
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
                        builder = new Friends.Builder(AndroidHttp.newCompatibleTransport(),
                                new AndroidJsonFactory(), null).setRootUrl("https://pacts-1027.appspot.com/_ah/api/");
                    }
                    friendsService = builder.build();
                }

                // Send the request
                try {
                    Message response = friendsService.sendFriendRequest(senderEmail, receiverEmail).execute();
                    return response.getSuccess();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSendFriendRequestTask = null;
            if (success) {
                Toast toast = Toast.makeText(context, getString(R.string.request_sent), Toast.LENGTH_SHORT);
                toast.show();
                dismiss();
            } else {
                mEmailEditText.setError(getString(R.string.error_user_does_not_exist));
                mEmailEditText.requestFocus();
            }
        }
    }
}
