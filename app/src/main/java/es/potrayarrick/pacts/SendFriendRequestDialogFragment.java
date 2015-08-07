package es.potrayarrick.pacts;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A dialog with an email input and a find button.
 */
public class SendFriendRequestDialogFragment extends DialogFragment {

    /**
     * The email input text.
     */
    private EditText mEmailEditText;

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_send_friend_request, container);

        mEmailEditText = (EditText) view.findViewById(R.id.email_input);
        Button mFindButton = (Button) view.findViewById(R.id.find_button);

        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email = mEmailEditText.getText().toString();
                if (Utils.isEmailValid(email)) {
                    // TODO send the request.
                    Toast toast = Toast.makeText(inflater.getContext(), getString(R.string.request_sent), Toast.LENGTH_SHORT);
                    toast.show();
                    dismiss();
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
}
