package educationapplication.onedaywiser;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ParentMessagesFragment extends ListFragment {

   private ArrayList<MessageThread> mMessageThreadItems = new ArrayList<MessageThread>();
    private Bundle bundle;

    public ParentMessagesFragment() {
    }

    public void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);

        bundle = this.getArguments();
        try{
            GetMessageThreads getMessageThreads = new GetMessageThreads();
            String jsonPostData = new JSONObject().put("child_id", bundle.getInt("child_id", 0)).toString();
            getMessageThreads.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_parent_messages, container, false);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

        MessageThread messageThread = mMessageThreadItems.get(position);
        ParentMessageThreadFragment fragment = new ParentMessageThreadFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("message_thread_id", messageThread.getId());
        fragment.setArguments(arguments);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class GetMessageThreads extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/messages/message_threads.php");
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
                        MessageThread messageThread = new MessageThread(jObj.getInt("id"),jObj.getString("message_topic"));
                        mMessageThreadItems.add(messageThread);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setListAdapter(new ParentMessagesFragment.MessageThreadListAdapter(mMessageThreadItems,getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MessageThreadListAdapter extends ArrayAdapter<MessageThread> {

        Context context;
        ArrayList<MessageThread> data;

        private class ViewHolder {
            TextView txtName;
        }

        public MessageThreadListAdapter(ArrayList<MessageThread> data,Context context) {
            super(context, R.layout.parent_message_thread_row, data);
            this.context = context;
            this.data = data;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            MessageThread dataModel = getItem(position);
            ParentMessagesFragment.MessageThreadListAdapter.ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ParentMessagesFragment.MessageThreadListAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.parent_message_thread_row, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.message_thread_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ParentMessagesFragment.MessageThreadListAdapter.ViewHolder) convertView.getTag();
            }

            viewHolder.txtName.setText(dataModel.getName());

            return convertView;
        }
    }
}
