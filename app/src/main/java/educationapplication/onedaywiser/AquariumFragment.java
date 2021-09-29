package educationapplication.onedaywiser;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class AquariumFragment extends Fragment {

    private FrameLayout mFrame;

    private ImageView mBottomLeftItem;
    private ImageView mBottomCenterItem;
    private ImageView mBottomRightItem;

    public AquariumFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Session mSession = new Session(getContext());
        View view = inflater.inflate(R.layout.fragment_aquarium, container, false);
        mFrame = (FrameLayout) view.findViewById(R.id.frame);

        mBottomLeftItem = (ImageView) view.findViewById(R.id.bottom_left_item);
        mBottomCenterItem = (ImageView) view.findViewById(R.id.bottom_center_item);
        mBottomRightItem = (ImageView) view.findViewById(R.id.bottom_right_item);

        try{
            GetAquariumItems getAquariumItems = new GetAquariumItems();
            String jsonPostData = new JSONObject().put("student_id", mSession.getUserId()).toString();
            getAquariumItems.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    static class GetAquariumItems extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/aquarium/user_items.php");
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
                    return new JSONObject(returnJSON);
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

        protected void onPostExecute(JSONObject jObj) {

            try {
                JSONArray topItems = jObj.getJSONArray("top_items");
                JSONArray bottomItems = jObj.getJSONArray("bottom_items");

                if(topItems.length() > 0){
                    for(int i = 0; i < topItems.length(); i++){
                        JSONObject j = topItems.getJSONObject(i);
                        ImageView iv = new ImageView(getContext());
                        new DownloadImageTask(iv).execute(j.getString("img_url"));

                        DisplayMetrics metrics = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                        Random r = new Random();
                        int Low = 150;
                        int High = 300;
                        int RandomHeight = r.nextInt(High-Low) + Low;

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RandomHeight, WRAP_CONTENT);


                        params.leftMargin = params.width
                                + new Random().nextInt(metrics.widthPixels - params.width);
                        params.topMargin = params.height
                                + new Random().nextInt(metrics.heightPixels - params.height);

                        params.bottomMargin = params.height + 300;
                        iv.setLayoutParams(params);

                        mFrame.addView(iv);
                    }
                }

                if(bottomItems.length() > 0){
                    for(int i = 0; i < bottomItems.length(); i++){

                        JSONObject j = bottomItems.getJSONObject(i);

                        switch(i) {
                            case 0:
                                new DownloadImageTask(mBottomLeftItem).execute(j.getString("img_url"));
                                break;
                            case 1:
                                new DownloadImageTask(mBottomCenterItem).execute(j.getString("img_url"));
                                break;
                            case 2:
                                new DownloadImageTask(mBottomRightItem).execute(j.getString("img_url"));
                                break;

                        }
                    }
                }
            } catch (Exception e) {
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
