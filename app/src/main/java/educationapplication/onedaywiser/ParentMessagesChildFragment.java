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

public class ParentMessagesChildFragment extends ListFragment {

    private Session mSession;
    private ArrayList<ParentChild> mParentChildItems = new ArrayList<ParentChild>();

    public ParentMessagesChildFragment() {
    }

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        mSession = new Session(getContext());

        try{
            GetChildren getChildren = new GetChildren();
            String jsonPostData = new JSONObject().put("parent_id", mSession.getUserId()).toString();
            getChildren.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_parent_messages_child, container, false);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

        ParentChild child = mParentChildItems.get(position);
        ParentMessagesFragment fragment = new ParentMessagesFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("child_id", child.getId());
        fragment.setArguments(arguments);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class GetChildren extends AsyncTask<String, Void, JSONArray> {

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
                        ParentChild child = new ParentChild(jObj.getInt("id"),jObj.getString("first_name") + " " + jObj.getString("last_name"));
                        mParentChildItems.add(child);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setListAdapter(new ParentMessagesChildFragment.ChildrenListAdapter(mParentChildItems,getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ChildrenListAdapter extends ArrayAdapter<ParentChild> {

        Context context;
        ArrayList<ParentChild> data;

        private class ViewHolder {
            TextView txtName;
        }

        public ChildrenListAdapter(ArrayList<ParentChild> data,Context context) {
            super(context, R.layout.parent_children_row, data);
            this.context = context;
            this.data = data;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ParentChild dataModel = getItem(position);
            ParentMessagesChildFragment.ChildrenListAdapter.ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ParentMessagesChildFragment.ChildrenListAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.parent_children_row, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.child_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ParentMessagesChildFragment.ChildrenListAdapter.ViewHolder) convertView.getTag();
            }

            viewHolder.txtName.setText(dataModel.getName());

            return convertView;
        }
    }
}
