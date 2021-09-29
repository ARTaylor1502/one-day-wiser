package educationapplication.onedaywiser;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends Activity {

    private EditText mEmailText;
    private EditText mPasswordText;
    private RadioGroup mRadioGroup;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailText= (EditText) findViewById(R.id.emailInput);
        mPasswordText = (EditText) findViewById(R.id.passwordInput);
        mRadioGroup = (RadioGroup) findViewById(R.id.login_radio);
        mLoginButton = (Button) findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonPostData = new JSONObject()
                            .put("email", mEmailText.getText().toString())
                            .put("password", mPasswordText.getText().toString());
                    if(mRadioGroup.getCheckedRadioButtonId() == R.id.student_radio){
                        jsonPostData.put("type", "student");
                    }else if(mRadioGroup.getCheckedRadioButtonId() == R.id.parent_radio){
                        jsonPostData.put("type", "parent");
                    }

                    String jsonPostDataString = jsonPostData.toString();
                    new Login().execute(jsonPostDataString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class Login extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/login.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(jsonData);
                writer.close();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        returnJSON += line + "\n";
                    }

                    bufferedReader.close();
                    return new JSONObject(returnJSON.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            if (result instanceof Object) {

                Session session = new Session(LoginActivity.this);

                if(result.has("student")){
                    try {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        session.startStudentSession(result.getJSONObject("student"));
                        finish();
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(result.has("parent")){
                    try {
                        Intent intent = new Intent(LoginActivity.this, ParentMainActivity.class);
                        session.startParentSession(result.getJSONObject("parent"));
                        finish();
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Toast.makeText(getApplicationContext(), "Invalid User Name or Password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
