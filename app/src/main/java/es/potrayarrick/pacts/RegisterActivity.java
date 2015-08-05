package es.potrayarrick.pacts;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;

import backend.pacts.potrayarrick.es.register.Register;
import backend.pacts.potrayarrick.es.register.model.Message;

/**
 * An activity for register a new user.
 */
public class RegisterActivity extends AppCompatActivity {

    //UI references
    /**
     * The email input.
     */
    private EditText mEmailView;
    /**
     * The password input.
     */
    private EditText mPasswordView;
    /**
     * The repeat password input.
     */
    private EditText mRepeatPasswordView;
    /**
     * The name input.
     */
    private EditText mNameView;
    /**
     * The surname input.
     */
    private EditText mSurnameView;

    /**
     * An ArrayList containing all form fields for doing loops.
     */
    private ArrayList<EditText> mFormFields;

    /**
     * The register service.
     */
    private static Register registrationService = null;

    /**
     * The asynchronous registration task.
     */
    private UserRegistrationTask mUserRegistrationTask;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        //Get UI references
        mEmailView = (EditText) findViewById(R.id.email_register_form);
        mPasswordView = (EditText) findViewById(R.id.password_register_form);
        mRepeatPasswordView = (EditText) findViewById(R.id.repeat_password_register_form);
        mNameView = (EditText) findViewById(R.id.name_register_form);
        mSurnameView = (EditText) findViewById(R.id.surname_register_form);

        //Populate mFormFields
        mFormFields = new ArrayList<>();
        mFormFields.add(mEmailView);
        mFormFields.add(mPasswordView);
        mFormFields.add(mRepeatPasswordView);
        mFormFields.add(mNameView);
        mFormFields.add(mSurnameView);

        //Button action listener
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Get values
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                String repeatPassword = mRepeatPasswordView.getText().toString();
                String name = mNameView.getText().toString();
                String surname = mSurnameView.getText().toString();

                boolean register = true;
                View focusView = null;

                //Check empty fields.
                for (EditText field : mFormFields) {
                    if (field.getText().toString().isEmpty() && register) {
                        register = false;
                        field.setError(getString(R.string.error_field_required));
                        focusView = field;
                    }
                }

                //Check email is valid.
                if (!Utils.isEmailValid(email) && register) {
                    register = false;
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    focusView = mEmailView;
                }

                //Check if password is valid.
                if (!Utils.isPasswordValid(password) && register) {
                    register = false;
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                    focusView = mPasswordView;
                }

                //Check if passwords match.
                if (!password.equals(repeatPassword) && register) {
                    register = false;
                    mRepeatPasswordView.setError(getString(R.string.passwords_match_error));
                    focusView = mRepeatPasswordView;
                }

                //If everything went right register.
                if (register) {
                    mUserRegistrationTask = new UserRegistrationTask(email, password, name, surname);
                    mUserRegistrationTask.execute();
                } else {
                    focusView.requestFocus();
                }

            }
        });
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * An asynchronous task for sending registration info to the backend.
     */
    private class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
        /**
         * The email of the new user.
         */
        private String email;
        /**
         * The password of the new user.
         */
        private String password;
        /**
         * The name of the new user.
         */
        private String name;
        /**
         * The surname of the new user.
         */
        private String surname;

        /**
         * Default constructor.
         * @param email new user email.
         * @param password new user password.
         * @param name new user name.
         * @param surname new user surname.
         */
        UserRegistrationTask(final String email, final String password, final String name, final String surname) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.surname = surname;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            if (registrationService == null) {  // Only do this once
                Register.Builder builder;
                if (Utils.LOCAL_TESTING) {
                    builder = new Register.Builder(AndroidHttp.newCompatibleTransport(),
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
                    builder = new Register.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null).setRootUrl("https://pacts-1027.appspot.com/_ah/api/");
                }

                registrationService = builder.build();
            }

            try {
                //Try to register the user.
                Message message = registrationService.userRegistration(email, password, name, surname).execute();
                //TODO check if this actually works...
                return message.getSuccess();
            } catch (IOException e) {
                Log.d("endpoint", e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        @Override
        protected final void onPostExecute(final Boolean success) {
            mUserRegistrationTask = null;

            if (success) {
                Log.d("post execute", "onPostExecute " + success.toString());
                finish();
                //TODO add a new intent.
            } else {
                mEmailView.setError(getString(R.string.email_exists_error));
                mEmailView.requestFocus();
            }
        }
    }
}
