package es.potrayarrick.pacts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import backend.pacts.potrayarrick.es.registration.Registration;
import backend.pacts.potrayarrick.es.registration.model.User;

class EndpointsAsyncTask extends AsyncTask<Pair<Context, Pair<String, String>>, Void, String> {
    private static Registration registrationService = null;
    private Context context;

    @Override
    protected String doInBackground(Pair<Context, Pair<String, String>>... params) {
        if(registrationService == null) {  // Only do this once
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            registrationService = builder.build();
        }

        this.context = params[0].first;
        String email = params[0].second.first;
        String password = params[0].second.second;

        try {
            User u = registrationService.userRegistration(email, password).execute();

            if (u != null){
                //Success
                return "Success!";
            }else return "Error";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}