package ro.duoline.agenti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Paul on 04.10.2017.
 */

public class DBController extends SQLiteOpenHelper {
    public DBController(Context applicationcontext){
        super(applicationcontext, "test.db", null, 2);
    }

    //Create Table

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query = "CREATE TABLE db_list (ID INTEGER PRIMARY KEY AUTOINCREMENT, firma TEXT, ip TEXT, nume_DB TEXT, user_DB TEXT, pass_DB TEXT)";
        db.execSQL(query);
        query = "CREATE TABLE acces (ID INTEGER PRIMARY KEY AUTOINCREMENT, id_gestiune INTEGER, user TEXT, parola TEXT, cod_gestiune TEXT, nr_gestiune INTEGER)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query;
        query = "DROP TABLE IF EXISTS db_list";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS acces";
        db.execSQL(query);
        onCreate(db);
    }

    public void deleteAllRecords(String table){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table,null,null);
        database.close();
    }

    /* Insert FIRME in database */
    public void insertFirme(HashMap<String, String> queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firma", queryValues.get("firma"));
        values.put("ip", queryValues.get("ip"));
        values.put("nume_DB", queryValues.get("nume_DB"));
        values.put("user_DB", queryValues.get("user_DB"));
        values.put("pass_DB", queryValues.get("pass_DB"));
        database.insert("db_list", null, values);
        database.close();

    }

    /* Insert USERI in database */
    public void insertUseri(HashMap<String, String> queryValues, HashMap<String, Integer> queryValuesInt){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_gestiune", queryValuesInt.get("id_gestiune"));
        values.put("user", queryValues.get("user"));
        values.put("parola", queryValues.get("parola"));
        values.put("cod_gestiune", queryValues.get("cod_gestiune"));
        values.put("nr_gestiune", queryValuesInt.get("nr_gestiune"));
        database.insert("acces", null, values);
        database.close();

    }

    public Boolean isFirmaInDB(String firm){
        String selectQuery = "SELECT * FROM db_list WHERE firma = '"+firm+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUserValid(String user, String pass){
        //String sel = "SELECT * FROM acces";
        String selectQuery = "SELECT * FROM acces WHERE user = '"+user+"' AND parola = '"+ pass +"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
     //   database.close();
        if (count > 0) {
        return true;
        } else {
            return false;
        }
    }

    synchronized public List<ContentValues> getDateConectare(String firm){
        List<ContentValues> data = new ArrayList<ContentValues>();
        ContentValues cv;
        String selectQuery = "SELECT * FROM db_list WHERE firma = '"+firm+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        cv = new ContentValues();
        cv.put("ip", cursor.getString(cursor.getColumnIndex("ip")));
        data.add(cv);
        cv = new ContentValues();
        cv.put("nume_DB", cursor.getString(cursor.getColumnIndex("nume_DB")));
        data.add(cv);
        cv = new ContentValues();
        cv.put("user_DB", cursor.getString(cursor.getColumnIndex("user_DB")));
        data.add(cv);
        cv = new ContentValues();
        cv.put("pass_DB", cursor.getString(cursor.getColumnIndex("pass_DB")));
        data.add(cv);
        database.close();
        return data;
    }
}
