package educationapplication.onedaywiser;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ExercisesFragment extends Fragment {

    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private ExerciseAdapter adapter;
    private Bundle bundle;
    private ImageView mSwipeLeftButton;
    private ImageView mSwipeRightButton;
    private ViewPager mViewPager;

    public ExercisesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);
        mSwipeLeftButton = (ImageView) view.findViewById(R.id.swipe_left);
        mSwipeRightButton = (ImageView) view.findViewById(R.id.swipe_right);
        mViewPager = (ViewPager)view.findViewById(R.id.exercise_pager);
        bundle = this.getArguments();
        try{
            GetExercises getExercises = new GetExercises();
            String jsonPostData = new JSONObject().put("lesson_id", bundle.getInt("lesson_id", 0)).toString();
            getExercises.execute(jsonPostData);
            adapter = new ExerciseAdapter(getChildFragmentManager());
            mViewPager.setAdapter(adapter);

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {
                }

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {

                    if(position == 0){
                        mSwipeLeftButton.setVisibility(View.INVISIBLE);
                    }else if(position +1 == mViewPager.getAdapter().getCount()){
                        mSwipeRightButton.setVisibility(View.INVISIBLE);
                    }else{
                        mSwipeLeftButton.setVisibility(View.VISIBLE);
                        mSwipeRightButton.setVisibility(View.VISIBLE);
                    }
                   System.out.println();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    public class ExerciseAdapter extends FragmentPagerAdapter {

        public ExerciseAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

    }


    class GetExercises extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/lesson/exercises.php");
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

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = null;
                try {
                    jObj = jsonArray.getJSONObject(i);
                    mFragments.add(ExerciseFragment.newInstance(bundle.getInt("lesson_id"),jObj.getInt("id"),jObj.getString("exercise_name"),jObj.getString("content"), jObj.getString("correct_answer")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private class PageListener {

    }
}