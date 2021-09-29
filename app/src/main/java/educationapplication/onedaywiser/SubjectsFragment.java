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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SubjectsFragment extends ListFragment{

    private Session mSession;
    private ArrayList<Subject> mSubjects = new ArrayList<Subject>();

    public SubjectsFragment() {
    }

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        mSession = new Session(getContext());
        GetSubjects getChildren = new GetSubjects();
        getChildren.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

        Subject subject = mSubjects.get(position);
        SubjectCategoriesFragment fragment = new SubjectCategoriesFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("subject_id", subject.getId());
        fragment.setArguments(arguments);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class GetSubjects extends AsyncTask<Void, Void, JSONArray> {

        protected JSONArray doInBackground(Void... args) {

            try {
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/subject/subjects.php");
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
                        Subject subject = new Subject(jObj.getInt("id"),jObj.getString("name"));
                        mSubjects.add(subject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setListAdapter(new SubjectsFragment.SubjectsListAdapter(mSubjects,getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SubjectsListAdapter extends ArrayAdapter<Subject> {

        Context context;
        ArrayList<Subject> data;

        private class ViewHolder {
            TextView txtName;
        }

        public SubjectsListAdapter(ArrayList<Subject> data,Context context) {
            super(context, R.layout.subject_row, data);
            this.context = context;
            this.data = data;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Subject dataModel = getItem(position);
            SubjectsFragment.SubjectsListAdapter.ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new SubjectsFragment.SubjectsListAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.subject_row, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.subject_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SubjectsFragment.SubjectsListAdapter.ViewHolder) convertView.getTag();
            }

            viewHolder.txtName.setText(dataModel.getName());

            return convertView;
        }
    }
}
