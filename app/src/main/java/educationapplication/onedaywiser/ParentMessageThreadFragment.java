package educationapplication.onedaywiser;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParentMessageThreadFragment extends Fragment {

    private LinearLayout mMessageThread;
    private Button mSendReplyButton;
    private EditText mMessageText;
    private Bundle bundle;
    private Session session;
    private Toast mMessageSentToast;
    private TextView mToastText;

    public ParentMessageThreadFragment() {
    }

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        session = new Session(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = this.getArguments();
        View view = inflater.inflate(R.layout.fragment_parent_message_thread, container, false);
        mMessageThread = (LinearLayout) view.findViewById(R.id.message_thread);
        mMessageText = (EditText) view.findViewById(R.id.parent_message);
        mSendReplyButton = (Button) view.findViewById(R.id.send_reply);

        View messageSentToastView = inflater.inflate(R.layout.message_sent, (ViewGroup) view.findViewById(R.id.toast_layout_root));
        mToastText = (TextView) messageSentToastView.findViewById(R.id.message_sent_text);
        mMessageSentToast = new Toast(getContext());
        mMessageSentToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        mMessageSentToast.setDuration(Toast.LENGTH_LONG);
        mMessageSentToast.setView(messageSentToastView);

        try{
            GetMessageThread getMessageThread = new GetMessageThread();
            String jsonPostData = new JSONObject().put("message_thread_id", bundle.getInt("message_thread_id", 0)).toString();
            getMessageThread.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSendReplyButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMessageText.getText().toString().matches("")){
                    try {
                        SendReply sendReply = new SendReply();
                        String jsonPostData = new JSONObject().put("message", mMessageText.getText().toString()).put("message_thread_id", bundle.getInt("message_thread_id", 0)).put("parent_id", session.getUserId()).toString();
                        sendReply.execute(jsonPostData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mToastText.setText("Please add text to your reply");
                    mMessageSentToast.show();
                }

            }
        });

        return view;
    }

    private class SendReply extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/messages/message_thread.php");
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
                mMessageThread.removeAllViews();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObj = null;
                    try {
                        jObj = jsonArray.getJSONObject(i);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = dateFormat.parse(jObj.getString("date_time"));

                        SimpleDateFormat sdf=new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
                        String dateString=sdf.format(date.getTime());
                        TextView tv = new TextView(getContext());
                        tv.setText(jObj.getString("first_name") + jObj.getString("last_name") + " - " + dateString);
                        tv.setTextSize(21);
                        tv.setBackgroundResource(R.drawable.secondary_button);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mMessageThread.addView(tv,params);

                        WebView webView = new WebView(getContext());
                        Document doc = Jsoup.parse(jObj.getString("message"));
                        Element head = doc.head();
                        head.append("<link rel='stylesheet' href='http://educationapp.artaylorwebdevelopment.com/styles/tiny-mce.css'>");
                        webView.loadData(doc.html(), "text/html; charset=utf-8", "UTF-8");
                        LinearLayout.LayoutParams webViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        webViewParams.setMargins(0,25,0,25);
                        mMessageThread.addView(webView,webViewParams);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mToastText.setText("Message Sent");
                mMessageSentToast.show();
                mMessageText.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetMessageThread extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/messages/message_thread.php");
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

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = dateFormat.parse(jObj.getString("date_time"));

                        SimpleDateFormat sdf=new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
                        String dateString=sdf.format(date.getTime());

                        TextView tv = new TextView(getContext());
                        tv.setText(jObj.getString("first_name") + jObj.getString("last_name") + " - " + dateString);
                        tv.setTextSize(21);
                        tv.setBackgroundResource(R.drawable.secondary_button);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mMessageThread.addView(tv,params);

                        WebView webView = new WebView(getContext());
                        Document doc = Jsoup.parse(jObj.getString("message"));
                        Element head = doc.head();
                        head.append("<link rel='stylesheet' href='http://educationapp.artaylorwebdevelopment.com/styles/tiny-mce.css'>");
                        webView.loadData(doc.html(), "text/html; charset=utf-8", "UTF-8");
                        LinearLayout.LayoutParams webViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        webViewParams.setMargins(0,25,0,25);
                        mMessageThread.addView(webView,webViewParams);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
