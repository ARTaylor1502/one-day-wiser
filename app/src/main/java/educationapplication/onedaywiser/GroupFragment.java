package educationapplication.onedaywiser;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GroupFragment extends Fragment {

    private ArrayList<Group> groupItems = new ArrayList<Group>();
    private BarChart mChartContainer;

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (FrameLayout) inflater.inflate(R.layout.fragment_group, container, false);
        mChartContainer = (BarChart) view.findViewById(R.id.group_leaderboard_chart);
        ConfigureChartSettings(mChartContainer);
        GetGroups groups = new GetGroups();
        groups.execute();
        return view;
    }

    private class GetGroups extends AsyncTask<Void, Void, JSONArray> {

        protected JSONArray doInBackground(Void... args) {

            try {
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/group/groups.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String jsonData = "";
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonData += line + "\n";
                    }
                    bufferedReader.close();
                    return new JSONArray(jsonData);
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
                        Group group = new Group(jObj.getString("name"),jObj.getString("img_url"),jObj.getInt("total_participation_points"),jObj.getInt("total_achievement_points"));
                        groupItems.add(group);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayList<String> labels = new ArrayList<>();
                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                ArrayList<BarEntry> behaviourPoints = new ArrayList<>();
                ArrayList<BarEntry> achievementPoints = new ArrayList<>();

                for(int i = 0 ; i < groupItems.size() ; i++){
                    Group group = groupItems.get(i);
                    labels.add(group.getName());
                    behaviourPoints.add(new BarEntry(group.getBehaviourPoints(),i));
                    achievementPoints.add(new BarEntry(group.getAchievementPoints(),i));
                }
                BarDataSet barDataSet1 = new BarDataSet(behaviourPoints, "Behaviour Points");
                barDataSet1.setColor(Color.rgb(255,250,117));
                BarDataSet barDataSet2 = new BarDataSet(achievementPoints, "Achievement Points");
                barDataSet2.setColor(Color.rgb(255,220,84));
                dataSets.add(barDataSet1);
                dataSets.add(barDataSet2);

                BarData data = new BarData(labels, dataSets);
                data.setValueTextSize(14f);
                mChartContainer.setData(data);
                mChartContainer.invalidate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ConfigureChartSettings(BarChart mChartContainer){
        mChartContainer.setDescription("");
        mChartContainer.getXAxis().setDrawGridLines(false);
        mChartContainer.getXAxis().setTextSize(14f);
        mChartContainer.getAxisLeft().setSpaceTop(5);
        mChartContainer.getAxisLeft().setTextSize(14f);
        mChartContainer.getAxisLeft().setDrawGridLines(false);
        mChartContainer.getAxisRight().setEnabled(false);
        XAxis xAxis = mChartContainer.getXAxis();
        mChartContainer.getAxisLeft().setStartAtZero(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend = mChartContainer.getLegend();
        legend.setTextSize(12f);
        legend.setFormSize(16f);
        mChartContainer.animateY(3000);
        mChartContainer.setNoDataText("Loading groups data");
    }
}
