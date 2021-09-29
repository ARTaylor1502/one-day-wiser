package educationapplication.onedaywiser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ParentCreateMessageFragment extends Fragment {

    private ArrayList<ParentChild> mParentChildren = new ArrayList<ParentChild>();
    private ArrayList<String> mParentChildrenNames = new ArrayList<String>();
    private ArrayList<String> mChildSchoolClassNames = new ArrayList<String>();
    private ArrayList<SchoolClass> mChildSchoolClasses = new ArrayList<SchoolClass>();
    private EditText mMessageTopic;
    private Spinner mParentChildrenSpinner;
    private Spinner mChildSchoolClassSpinner;
    private EditText mMessage;
    private Session mSession;
    private Button mCreateMessageButton;
    private Toast mMessageThreadSentToast;
    private TextView mToastText;

    public ParentCreateMessageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSession = new Session(getContext());
        View view = inflater.inflate(R.layout.fragment_parent_create_message, container, false);
        mParentChildrenSpinner = (Spinner)view.findViewById(R.id.child_concerning);
        mChildSchoolClassSpinner = (Spinner)view.findViewById(R.id.child_school_class);
        mCreateMessageButton = (Button)view.findViewById(R.id.create_message_thread);
        mMessageTopic = (EditText)view.findViewById(R.id.parent_message_topic);
        mMessage = (EditText)view.findViewById(R.id.message);

        View messageSentToastView = inflater.inflate(R.layout.message_thread_sent, (ViewGroup) view.findViewById(R.id.toast_layout_root));
        mToastText = (TextView) messageSentToastView.findViewById(R.id.message_thread_text);
        mMessageThreadSentToast = new Toast(getContext());
        mMessageThreadSentToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        mMessageThreadSentToast.setDuration(Toast.LENGTH_LONG);
        mMessageThreadSentToast.setView(messageSentToastView);


        try{
            GetChildren getChildren = new GetChildren();
            String jsonPostData = new JSONObject().put("parent_id", mSession.getUserId()).toString();
            getChildren.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mParentChildrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ParentChild parentChild = mParentChildren.get(position);

                try{
                    UpdateSchoolClass updateSchoolClass = new UpdateSchoolClass();
                    String jsonPostData = new JSONObject().put("student_id", parentChild.getId()).toString();
                    updateSchoolClass.execute(jsonPostData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        mCreateMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParentChild parentChild = mParentChildren.get( mParentChildrenSpinner.getSelectedItemPosition());
                SchoolClass schoolClass = mChildSchoolClasses.get(mChildSchoolClassSpinner.getSelectedItemPosition());
                if(!mMessageTopic.getText().toString().matches("") && !mMessage.getText().toString().matches("")){
                    try{
                        CreateMessageThread createMessageThread = new CreateMessageThread();
                        String jsonPostData = new JSONObject().put("student_id", parentChild.getId()).put("parent_id",mSession.getUserId()).put("school_class_id",schoolClass.getId()).put("message_topic",mMessageTopic.getText().toString()).put("message",mMessage.getText().toString()).toString();
                        createMessageThread.execute(jsonPostData);
                        mToastText.setText("Message Thread Sent");
                        mMessageThreadSentToast.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mToastText.setText("Please fill in all details before submitting");
                    mMessageThreadSentToast.show();
                }

            }
        });
        return view;
    }

    public class GetChildren extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/messages/children.php");
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
                    return new JSONArray(returnJSON);
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("Error", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(JSONArray jsonArray) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObj = null;
                    try {
                        jObj = jsonArray.getJSONObject(i);
                        ParentChild parentChild = new ParentChild(jObj.getInt("id"),jObj.getString("first_name") + " " + jObj.getString("last_name"));
                        mParentChildren.add(parentChild);
                        mParentChildrenNames.add(jObj.getString("first_name") + " " + jObj.getString("last_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, mParentChildrenNames);
                mParentChildrenSpinner.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UpdateSchoolClass extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/messages/school_classes.php");
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
                    return new JSONArray(returnJSON);
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("Error", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(JSONArray jsonArray) {
            try {
                mChildSchoolClassNames.clear();
                mChildSchoolClasses.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObj = null;
                    try {
                        jObj = jsonArray.getJSONObject(i);
                        SchoolClass schoolClass = new SchoolClass(jObj.getInt("id"),jObj.getString("name"));
                        mChildSchoolClasses.add(schoolClass);
                        mChildSchoolClassNames.add(jObj.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, mChildSchoolClassNames);
                adapter.notifyDataSetChanged();
                mChildSchoolClassSpinner.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CreateMessageThread extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/messages/create_message_thread.php");
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

                    return null;
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("Error", e.getMessage(), e);
                return null;
            }
        }
    }
}
