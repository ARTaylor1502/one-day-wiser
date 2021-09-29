package educationapplication.onedaywiser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LessonFragment extends Fragment {
    private TextView mName;
    private WebView mWebView;

    public LessonFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);
        final Bundle bundle = this.getArguments();
        mName = (TextView)view.findViewById(R.id.lesson_name);
        mWebView = (WebView)view.findViewById(R.id.webview);
        mWebView.setVerticalScrollBarEnabled(true);
        Button mExercisesButton = (Button) view.findViewById(R.id.navigate_to_exercises);
        mExercisesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExercisesFragment fragment = new ExercisesFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("lesson_id", bundle.getInt("lesson_id", 0));
                fragment.setArguments(arguments);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        try{
            GetLesson getLesson = new GetLesson();
            String jsonPostData = new JSONObject().put("lesson_id", bundle.getInt("lesson_id", 0)).toString();
            getLesson.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    class GetLesson extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/lesson/lesson.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(jsonData);
                writer.close();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        returnJSON += line + "\n";
                    }
                    returnJSON = returnJSON.toString();
                    bufferedReader.close();
                    return new JSONObject(returnJSON);
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

        protected void onPostExecute(JSONObject jsonObject) {
            try {
                Document doc = Jsoup.parse(jsonObject.getString("content"));
                Elements links = doc.select("img");
                links.attr("style", "max-width:100%;height:auto;text-align:center;");
                mName.setText(jsonObject.getString("name"));
                mWebView.loadData(doc.html(), "text/html; charset=utf-8", "UTF-8");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
