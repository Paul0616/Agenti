package ro.duoline.agenti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by Paul on 04.10.2017.
 */

public class DBController extends SQLiteOpenHelper {
    public DBController(Context applicationcontext){
        super(applicationcontext, "test.db", null, 1);
    }

    //Create Table

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query = "CREATE TABLE db_list (ID INTEGER PRIMARY KEY AUTOINCREMENT, firma TEXT, ip TEXT, nume_DB TEXT, user_DB TEXT, pass_DB TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query;
        query = "DROP TABLE IF EXISTS db_list";
        db.execSQL(query);
        onCreate(db);
    }

    public void deleteAllRecords(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("db_list",null,null);
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
}
