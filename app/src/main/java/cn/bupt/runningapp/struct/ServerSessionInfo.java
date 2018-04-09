package cn.bupt.runningapp.struct;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by yisic on 2017/7/5.
 */

public class ServerSessionInfo {
    public static String SocialUID;
    public static String SocialToken;
    public static String SocialRefreshToken;
    public static String SocialType;
    public static String AppToken;

    public static void init(String socialUID, String socialToken, String socialRefreshToken, String socialType, String appToken, Application application) {
        SocialUID = socialUID;
        SocialToken = socialToken;
        SocialRefreshToken = socialRefreshToken;
        SocialType = socialType;
        AppToken = appToken;
        SharedPreferences sharedPreferences = application.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("have_saved_login_info", true);
        editor.putString("social_uid", SocialUID);
        editor.putString("social_token", SocialToken);
        editor.putString("social_refresh_token", SocialRefreshToken);
        editor.putString("social_type", SocialType);
        editor.putString("app_token", AppToken);
        editor.apply();
    }

    public static void load(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        SocialUID = sharedPreferences.getString("social_uid", "");
        SocialToken = sharedPreferences.getString("social_token", "");
        SocialRefreshToken = sharedPreferences.getString("social_refresh_token", "");
        SocialType = sharedPreferences.getString("social_type", "");
        AppToken = sharedPreferences.getString("app_token", "");
    }

    public static boolean haveSavedLoginInfo(Application application) {
        return application.getSharedPreferences("login_info", Activity.MODE_PRIVATE)
                .getBoolean("have_saved_login_info", false);
    }

    public static void clean(Application application) {
        SocialUID = "";
        SocialToken = "";
        SocialRefreshToken = "";
        SocialType = "";
        AppToken = "";
        SharedPreferences sharedPreferences = application.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("have_saved_login_info", false);
        editor.apply();
    }
}
