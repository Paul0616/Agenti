package ro.duoline.agenti;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Paul on 10/5/2017.
 */

public class SaveSharedPreference {
    //static final boolean LOGGED = false;

    static SharedPreferences getSharedPreference(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLoggedIn(Context ctx){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putBoolean("LOGGED", true);
        editor.commit();
    }

    public static void setLogOut(Context ctx){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

    public static boolean getLogged(Context ctx){
        return getSharedPreference(ctx).getBoolean("LOGGED", false);
    }
}
