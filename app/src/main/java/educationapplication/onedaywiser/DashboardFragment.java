package educationapplication.onedaywiser;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class DashboardFragment extends Fragment {

    private Session session;
    private TextView mTrophiesCount;
    private TextView mExercisesCompletedCount;
    private TextView mParticipationPointsCount;
    private TextView mAchievementPointsCount;
    private TextView mGroupName;

    public DashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getContext());
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mTrophiesCount = (TextView) view.findViewById(R.id.dashboard_trophies_count);
        mExercisesCompletedCount = (TextView) view.findViewById(R.id.dashboard_exercises_completed_count);
        mParticipationPointsCount = (TextView) view.findViewById(R.id.dashboard_participation_points_count);
        mAchievementPointsCount = (TextView) view.findViewById(R.id.dashboard_achievement_points_count);
        mGroupName = (TextView) view.findViewById(R.id.dashboard_group_name);
        TextView mWelcomeMessage = (TextView) view.findViewById(R.id.welcome_message);
        mWelcomeMessage.setText("Welcome " + session.getUserName() + "!");

        try{
            GetDashboardInfo getDashboard = new GetDashboardInfo();
            String jsonPostData = new JSONObject().put("student_id", session.getUserId()).toString();
            getDashboard.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    static class GetDashboardInfo extends AsyncTask<String, Void, JSONObject> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected JSONObject doInBackground(String... args) {

            try {
                String jsonData = args[0];
                StringBuilder returnJSON = new StringBuilder();
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/dashboard/dashboard.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(jsonData);
                writer.close();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        returnJSON.append(line).append("\n");
                    }
                    return new JSONObject(returnJSON.toString());
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
                mTrophiesCount.setText(jsonObject.getString("trophies"));
                mExercisesCompletedCount.setText(jsonObject.getString("completed_exercises"));
                mParticipationPointsCount.setText(session.getParticipationPoints());
                mAchievementPointsCount.setText(session.getAchievementPoints());
                mGroupName.setText(jsonObject.getString("group_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
