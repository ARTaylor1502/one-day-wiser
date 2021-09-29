package educationapplication.onedaywiser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExerciseFragment extends Fragment {
    private TextView mName;
    private WebView mContent;
    private EditText mUserAnswer;
    private Button mSubmitAnswer;
    private static Bundle mBundle;
    private Session session;

    private int lessonId;
    private int exerciseId;
    private String name;
    private Document content;
    private String correctAnswer;

    private ImageView mToastImage;
    private TextView mToastHeading;
    private TextView mToastText;
    private TextView mToastAchievementPointsText;

    private TextView mTrophyFeedbackSubHeading;
    private LinearLayout mNewTrophiesContainer;
    private Toast mTrophyToast;

    public ExerciseFragment() {}

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        session = new Session(getContext());
    }

    public static ExerciseFragment newInstance(int lessonId, int exerciseId, String name, String content , String correctAnswer) {
        mBundle = new Bundle();
        mBundle.putInt("lesson_id", lessonId);
        mBundle.putInt("exercise_id", exerciseId);
        mBundle.putString("name", name);
        mBundle.putString("content", content);
        mBundle.putString("correct_answer",correctAnswer);
        ExerciseFragment fragment = new ExerciseFragment();
        fragment.setArguments(mBundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            lessonId = bundle.getInt("lesson_id");
            exerciseId = bundle.getInt("exercise_id");
            name = bundle.getString("name");
            content = Jsoup.parse(bundle.getString("content"));
            Element head = content.head();
            head.append("<link rel='stylesheet' href='http://educationapp.artaylorwebdevelopment.com/styles/tiny-mce.css'>");
            correctAnswer = bundle.getString("correct_answer");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        mName = (TextView) view.findViewById(R.id.exercise_name);
        mContent = (WebView) view.findViewById(R.id.exercise_content);
        mUserAnswer = (EditText) view.findViewById(R.id.exercise_answer);
        mSubmitAnswer = (Button) view.findViewById(R.id.submit_answer);

        //Answer Feedback Toast
        View answerFeedbackToastView = inflater.inflate(R.layout.answer_feedback, (ViewGroup) view.findViewById(R.id.toast_layout_root));
        mToastImage = (ImageView) answerFeedbackToastView.findViewById(R.id.feedback_image);
        mToastImage.setImageResource(R.drawable.owl_icon);
        mToastHeading = (TextView) answerFeedbackToastView.findViewById(R.id.feedback_heading);
        mToastText = (TextView) answerFeedbackToastView.findViewById(R.id.feedback_text);
        mToastAchievementPointsText = (TextView) answerFeedbackToastView.findViewById(R.id.feedback_achievement_points);
        mToastAchievementPointsText.setText("");

        final Toast answerFeedbackToast = new Toast(getContext());
        answerFeedbackToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        answerFeedbackToast.setDuration(Toast.LENGTH_LONG);
        answerFeedbackToast.setView(answerFeedbackToastView);

        //Trophy Toast
        View trophiesToastView = inflater.inflate(R.layout.unlocked_trophies, (ViewGroup) view.findViewById(R.id.trophy_toast_layout_root));
        mNewTrophiesContainer = (LinearLayout) trophiesToastView.findViewById(R.id.new_trophies);
        mTrophyFeedbackSubHeading = (TextView) trophiesToastView.findViewById(R.id.trophy_feedback_subheading);

        mTrophyToast = new Toast(getContext());
        mTrophyToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        mTrophyToast.setDuration(Toast.LENGTH_LONG);
        mTrophyToast.setView(trophiesToastView);

        readBundle(getArguments());

        mName.setText(name);
        mContent.loadData(content.html(), "text/html; charset=utf-8", "UTF-8");

        mSubmitAnswer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserAnswer.getText().toString().equals(correctAnswer)){
                    mToastImage.setImageResource(R.drawable.owl_icon_tipped_hat);
                    mToastHeading.setText("Well Done!");
                    mToastText.setText("That is the correct answer!");
                    try{
                        SaveExerciseCompletion saveExercise = new SaveExerciseCompletion();
                        String jsonPostData = new JSONObject().put("exercise_id", exerciseId).put("student_id",session.getUserId()).toString();
                        saveExercise.execute(jsonPostData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mToastHeading.setText("Incorrect");
                    mToastText.setText("Please try again");
                }

                answerFeedbackToast.show();
            }
        });
        return view;
    }


    class SaveExerciseCompletion extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... args) {
            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/exercise/exercise.php");
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
                   return new JSONObject(returnJSON);
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("Error", e.getMessage(), e);
            }
            return null;
        }

        protected void onPostExecute(JSONObject jsonObject) {

            try {

                JSONArray trophiesArray = jsonObject.getJSONArray("unlocked_trophies");

                if(jsonObject.has("unlocked_achievement_points") && jsonObject.has("new_achievement_points") && jsonObject.has("new_available_points")){
                    mToastAchievementPointsText.setText("You have been awarded " + jsonObject.getString("unlocked_achievement_points") + " achievement points");
                    session.setAchievementPoints(jsonObject.getString("new_achievement_points"));
                    session.setAvailablePoints(jsonObject.getString("new_available_points"));
                    ((MainActivity)getActivity()).setAchievementPoints(session.getAchievementPoints());
                }



                if(trophiesArray != null && trophiesArray.length() != 0){
                    int TotalAdditionalPoints = 0;
                    for (int i = 0; i < trophiesArray.length(); i++) {
                        JSONObject row = trophiesArray.getJSONObject(i);
                        String trophyName = row.getString("name");
                        String trophyUrl = row.getString("img_url");
                        String cost = row.getString("achievement_points");
                        TotalAdditionalPoints += Integer.parseInt(cost);
                        LinearLayout ll = new LinearLayout(getContext());
                        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        llparams.setMargins(0,0,0,15);


                        ImageView iv = new ImageView(getContext());
                        new DownloadImageTask(iv).execute(trophyUrl);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(75,75);
                        params.setMargins(0,0,15,0);
                        iv.setLayoutParams(params);
                        ll.addView(iv);

                        LinearLayout.LayoutParams tvparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView tv = new TextView(getContext());
                        tv.setText(trophyName);
                        tv.setLayoutParams(tvparams);
                        ll.addView(tv);

                        ll.setLayoutParams(llparams);

                        mNewTrophiesContainer.addView(ll);
                    }



                    if(trophiesArray.length() > 1){
                        mTrophyFeedbackSubHeading.setText("You have unlocked " + trophiesArray.length() + " new trophies and " + TotalAdditionalPoints + " additional achievement points");
                    }else{
                        mTrophyFeedbackSubHeading.setText("You have unlocked a new trophy and " + TotalAdditionalPoints + " additional achievement points");
                    }

                    mTrophyToast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
