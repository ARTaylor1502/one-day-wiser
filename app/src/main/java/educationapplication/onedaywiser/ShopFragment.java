package educationapplication.onedaywiser;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ShopFragment extends Fragment {

    private Session mSession;
    private LinearLayout mFishItems;
    private LinearLayout mBottomTankItems;
    private TextView mAvailablePoints;
    private ArrayList<ShopItem> mTopShopItems = new ArrayList<ShopItem>();
    private ArrayList<ShopItem> mBottomShopItems = new ArrayList<ShopItem>();
    private Dialog mPurchaseDialog;
    private TextView mPurchaseItemText;
    private Button mYesButton;
    private Button mNoButton;
    private ShopItem mSelectedShopItem;

    public ShopFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        mFishItems = (LinearLayout) view.findViewById(R.id.fish_items);
        mBottomTankItems = (LinearLayout) view.findViewById(R.id.bottom_shop_items);
        mAvailablePoints = (TextView) view.findViewById(R.id.available_points);

        mPurchaseDialog = new Dialog(getContext());
        mPurchaseDialog.setContentView(R.layout.purchase_item);
        mPurchaseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPurchaseItemText = (TextView) mPurchaseDialog.findViewById(R.id.purchase_item_text);
        mYesButton = (Button) mPurchaseDialog.findViewById(R.id.yes_button);
        mNoButton = (Button) mPurchaseDialog.findViewById(R.id.no_button);

        mYesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mSelectedShopItem.getCost() > Integer.parseInt(mSession.getAvailablePoints())){

                    mPurchaseItemText.setText("Sorry you don't have enough points to buy this item");
                    mYesButton.setVisibility(View.GONE);
                    mNoButton.setVisibility(View.GONE);

                }else{
                    try {
                        PurchaseShopItem purchaseItems = new PurchaseShopItem();
                        String jsonPostData = null;
                        jsonPostData = new JSONObject().put("student_id",mSession.getUserId()).put("shop_item_id",mSelectedShopItem.getId()).put("spent_points",mSelectedShopItem.getCost()).toString();
                        purchaseItems.execute(jsonPostData);
                        mPurchaseItemText.setText("Congratulations you have purchased the " + mSelectedShopItem.getName() + "!");
                        mYesButton.setVisibility(View.GONE);
                        mNoButton.setVisibility(View.GONE);

                        mFishItems.removeAllViews();
                        mBottomTankItems.removeAllViews();
                        mTopShopItems.clear();
                        mBottomShopItems.clear();
                        GetShopItems getShopItems = new GetShopItems();
                        try {
                            String jPostData = new JSONObject().put("student_id",mSession.getUserId()).toString();
                            getShopItems.execute(jPostData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPurchaseDialog.dismiss();
            }
        });

        mSession = new Session(getContext());
        mAvailablePoints.setText(mSession.getAvailablePoints());
        GetShopItems getShopItems = new GetShopItems();
        try {
            String jsonPostData = new JSONObject().put("student_id",mSession.getUserId()).toString();
            getShopItems.execute(jsonPostData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    class GetShopItems extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/shop/remaining_items.php");
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
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
                        params.setMargins(0,0,25,0);
                        iv.setLayoutParams(params);
                        new DownloadImageTask(iv).execute(j.getString("img_url"));
                        ShopItem shopItem = new ShopItem(j.getInt("id"),j.getString("name"),new URL(j.getString("img_url")),j.getInt("cost"));
                        mTopShopItems.add(shopItem);
                        iv.setTag(i);
                        iv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                int position  = (Integer) v.getTag();
                                mSelectedShopItem = mTopShopItems.get(position);
                                mPurchaseItemText.setText("Would you like to buy the " + mSelectedShopItem.getName() + " for " + mSelectedShopItem.getCost() + " points?");
                                mYesButton.setVisibility(View.VISIBLE);
                                mNoButton.setVisibility(View.VISIBLE);
                                mPurchaseDialog.show();
                            }
                        });
                        mFishItems.addView(iv);
                    }
                }

                if(bottomItems.length() > 0){
                    for(int i = 0; i < bottomItems.length(); i++){
                        JSONObject j = bottomItems.getJSONObject(i);
                        ImageView iv = new ImageView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
                        params.setMargins(0,0,25,0);
                        iv.setLayoutParams(params);
                        new DownloadImageTask(iv).execute(j.getString("img_url"));
                        ShopItem shopItem = new ShopItem(j.getInt("id"),j.getString("name"),new URL(j.getString("img_url")),j.getInt("cost"));
                        mBottomShopItems.add(shopItem);
                        iv.setTag(i);
                        iv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                int position  = (Integer) v.getTag();
                                mSelectedShopItem = mBottomShopItems.get(position);
                                mPurchaseItemText.setText("Would you like to buy the " + mSelectedShopItem.getName() + " for " + mSelectedShopItem.getCost() + " points?");
                                mPurchaseDialog.show();

                                mYesButton.setVisibility(View.VISIBLE);
                                mNoButton.setVisibility(View.VISIBLE);
                                mPurchaseDialog.show();

                            }
                        });
                        mBottomTankItems.addView(iv);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PurchaseShopItem extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... args) {

            try {
                String jsonData = args[0];
                String returnJSON = "";
                URL url = new URL("http://educationapp.artaylorwebdevelopment.com/webservice/shop/purchase_item.php");
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

                String availablePoints = jObj.getString("available_points");
                mSession.setAvailablePoints(availablePoints);
                mAvailablePoints.setText(availablePoints);

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
