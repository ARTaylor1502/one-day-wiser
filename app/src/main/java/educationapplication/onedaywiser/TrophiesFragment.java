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
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class TrophiesFragment extends Fragment {
    private Session session;
    private LinearLayout trophy_row1;
    private LinearLayout trophy_row2;
    private LinearLayout trophy_row3;

    public TrophiesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trophies, container, false);
        trophy_row1 = (LinearLayout) view.findViewById(R.id.trophy_row1);
        trophy_row2 = (LinearLayout) view.findViewById(R.id.trophy_row2);
        trophy_row3 = (LinearLayout) view.findViewById(R.id.trophy_row3);
        session = new Session(getContext());

        try{
            GetTrophies getTrophies = new GetTrophies();
            String jsonPostData = new JSONObject().put("student_id",session.getUserId()).toString();
            getTrophies.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    class GetTrophies extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/trophy/trophies.php");
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
                    ImageView iv = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,0.25f);
                    params.gravity = Gravity.BOTTOM;
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    iv.setAdjustViewBounds(true);
                    iv.setId(jObj.getInt("id"));
                    iv.setPadding(15,0,15,0);
                    iv.setLayoutParams(params);
                    new DownloadImageTask(iv).execute(jObj.getString("img_url"));
                    trophy_row1.addView(iv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
