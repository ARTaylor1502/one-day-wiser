package educationapplication.onedaywiser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;


public class Session {

    private SharedPreferences prefs;

    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void startStudentSession(JSONObject user) throws JSONException {
        prefs.edit()
        .putString("first_name", user.getString("first_name"))
        .putString("last_name", user.getString("last_name"))
        .putString("user_id", user.getString("id"))
        .putString("participation_points", user.getString("participation_points"))
        .putString("achievement_points", user.getString("achievement_points"))
        .putString("available_points", user.getString("available_points"))
        .apply();
    }

    public void startParentSession(JSONObject user) throws JSONException {
        prefs.edit()
                .putString("first_name", user.getString("first_name"))
                .putString("last_name", user.getString("last_name"))
                .putString("user_id", user.getString("id"))
                .apply();
    }

    public String getUserName() {
        return prefs.getString("first_name", "");
    }

    public String getUserId() {
        return prefs.getString("user_id", "");
    }

    public String getParticipationPoints() {
        String points = "0";
        if(prefs.contains("participation_points")) {
            points = prefs.getString("participation_points", "");
        }
        return points;
    }

    public void setParticipationPoints(String particpation_points) {
        prefs.edit().putString("participation_points", particpation_points).apply();
    }

    public String getAchievementPoints() {
        String points = "0";
        if(prefs.contains("achievement_points")) {
            points = prefs.getString("achievement_points", "");
        }
        return points;
    }

    public void setAchievementPoints(String achievement_points) {
        prefs.edit().putString("achievement_points", achievement_points).apply();
    }

    public String getAvailablePoints() {
        String points = "0";
        if(prefs.contains("available_points")) {
            points = prefs.getString("available_points", "");
        }
        return points;
    }

    public void setAvailablePoints(String available_points) {
        prefs.edit().putString("available_points", available_points).apply();
    }

    public void endSession(){
        prefs.edit().clear().apply();
    }
}
