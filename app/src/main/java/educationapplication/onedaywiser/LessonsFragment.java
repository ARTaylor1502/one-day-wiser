package educationapplication.onedaywiser;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LessonsFragment extends ListFragment {

    private ArrayList<Lesson> lessonItems = new ArrayList<Lesson>();

    public LessonsFragment() {}

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        GetLessons lessons = new GetLessons();
        final Bundle bundle = this.getArguments();
        try{
            String jsonPostData = new JSONObject().put("subject_category_id", bundle.getInt("subject_category_id", 0)).toString();
            lessons.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lessons, container, false);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Lesson lesson = lessonItems.get(position);
        LessonFragment fragment = new LessonFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("lesson_id", lesson.getId());
        fragment.setArguments(arguments);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class GetLessons extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {

                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/lesson/lessons.php");
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
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObj = null;
                    try {
                        jObj = jsonArray.getJSONObject(i);
                        Lesson lesson = new Lesson(jObj.getInt("id"),jObj.getString("name"));
                        lessonItems.add(lesson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setListAdapter(new LessonListAdapter(lessonItems,getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LessonListAdapter extends ArrayAdapter<Lesson> {

        Context context;
        ArrayList<Lesson> data;

        private class ViewHolder {
            TextView txtName;
        }

        public LessonListAdapter(ArrayList<Lesson> data,Context context) {
            super(context, R.layout.lessons_row, data);
            this.context = context;
            this.data = data;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Lesson dataModel = getItem(position);
            ViewHolder viewHolder;

            final View result;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.lessons_row, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.lesson_name);
                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }

            viewHolder.txtName.setText(dataModel.getName());

            return convertView;
        }
    }

}
