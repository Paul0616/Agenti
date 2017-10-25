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

    public static void setStyle(Context context, int style){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt("STYLE", style);
        editor.commit();
    }

    public static int getStyle(Context ctx){
        return getSharedPreference(ctx).getInt("STYLE", R.style.stilDark);
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

    public static void setFirma(Context ctx, String firm){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("FIRMA", firm);
        editor.commit();
    }

    public static void setLastRefreshTime(Context ctx, long time)
    {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putLong("LASTREFRESH", time);
        editor.commit();
    }

    public static long getLastRefreshTime(Context ctx){
        return getSharedPreference(ctx).getLong("LASTREFRESH", 0);
    }

    public static void setDebit(Context ctx, String debit){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("DEBIT", debit);
        editor.commit();
    }

    public static void setUser(Context ctx, String user){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("USER", user);
        editor.commit();
    }

    public static boolean getLogged(Context ctx){
        return getSharedPreference(ctx).getBoolean("LOGGED", false);
    }
}
