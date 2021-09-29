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

public class SubjectCategoriesFragment extends ListFragment {

    private ArrayList<SubjectCategory> mSubjectCategories = new ArrayList<SubjectCategory>();

    public SubjectCategoriesFragment() {
    }

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        GetSubjectCategories getSubjectCategories = new GetSubjectCategories();
        final Bundle bundle = this.getArguments();
        try{
            String jsonPostData = new JSONObject().put("subject_id", bundle.getInt("subject_id", 0)).toString();
            getSubjectCategories.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subject_categories, container, false);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

        SubjectCategory subject = mSubjectCategories.get(position);
        LessonsFragment fragment = new LessonsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("subject_category_id", subject.getId());
        fragment.setArguments(arguments);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class GetSubjectCategories extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {

                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/subject/subject_categories.php");
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
                        SubjectCategory subjectCategory = new SubjectCategory(jObj.getInt("id"),jObj.getString("name"));
                        mSubjectCategories.add(subjectCategory);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setListAdapter(new SubjectCategoriesFragment.SubjectCategoriesListAdapter(mSubjectCategories,getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SubjectCategoriesListAdapter extends ArrayAdapter<SubjectCategory> {

        Context context;
        ArrayList<SubjectCategory> data;

        private class ViewHolder {
            TextView txtName;
        }

        public SubjectCategoriesListAdapter(ArrayList<SubjectCategory> data,Context context) {
            super(context, R.layout.subject_row, data);
            this.context = context;
            this.data = data;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            SubjectCategory dataModel = getItem(position);
            SubjectCategoriesFragment.SubjectCategoriesListAdapter.ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new SubjectCategoriesFragment.SubjectCategoriesListAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.subject_category_row, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.subject_category_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SubjectCategoriesFragment.SubjectCategoriesListAdapter.ViewHolder) convertView.getTag();
            }

            viewHolder.txtName.setText(dataModel.getName());

            return convertView;
        }
    }
}
