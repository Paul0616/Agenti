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
        super(applicationcontext, "test.db", null, 6);
    }

    //Create Table

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query = "CREATE TABLE db_list (ID INTEGER PRIMARY KEY AUTOINCREMENT, firma TEXT, ip TEXT, nume_DB TEXT, user_DB TEXT, pass_DB TEXT)";
        db.execSQL(query);
        query = "CREATE TABLE acces (ID INTEGER PRIMARY KEY AUTOINCREMENT, id_gestiune INTEGER, user TEXT, parola TEXT, cod_gestiune TEXT, nr_gestiune INTEGER, nume_gestiune TEXT, debit TEXT, pozitie_pret INTEGER)";
        db.execSQL(query);
        query = "CREATE TABLE produse (ID INTEGER PRIMARY KEY AUTOINCREMENT, cod INTEGER, stoc INTEGER, rezervata INTEGER, clasa TEXT, denumire TEXT, um TEXT, tva INTEGER, pret_livr REAL)";
        db.execSQL(query);
        query = "CREATE TABLE cos (ID INTEGER PRIMARY KEY AUTOINCREMENT, cod INTEGER, comandate INTEGER)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query;
        query = "DROP TABLE IF EXISTS db_list";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS acces";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS produse";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS cos";
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
        values.put("nume_gestiune", queryValues.get("nume_gestiune"));
        values.put("debit", queryValues.get("debit"));
        values.put("pozitie_pret", queryValuesInt.get("pozitie_pret"));
        database.insert("acces", null, values);
        database.close();

    }

    /* Insert PRODUSE in database */
    public void insertProduse(HashMap<String, String> queryValues, HashMap<String, Integer> queryValuesInt, HashMap<String, Double> queryValuesFloat, SQLiteDatabase database){

        //SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cod", queryValuesInt.get("cod"));
        values.put("stoc", queryValuesInt.get("stoc"));
        values.put("rezervata", queryValuesInt.get("rezervata"));
        values.put("clasa", queryValues.get("clasa"));
        values.put("denumire", queryValues.get("denumire"));
        values.put("um", queryValues.get("um"));
        values.put("tva", queryValuesInt.get("tva"));
        values.put("pret_livr", queryValuesFloat.get("pret_livr"));

        database.insert("produse", null, values);



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
        database.close();
        if (count > 0) {
        return true;
        } else {
            return false;
        }
    }

    public ContentValues getDebit(String user, String pass){
        //String sel = "SELECT * FROM acces";
        String selectQuery = "SELECT * FROM acces WHERE user = '"+user+"' AND parola = '"+ pass +"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        ContentValues cv;
        cv = new ContentValues();
        cv.put("debit", cursor.getString(cursor.getColumnIndex("debit")));
        database.close();
        return cv;

    }

    synchronized public String getDateConectare(String firm){
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
        String dateconectare = data.get(0).get("ip").toString()+","+data.get(1).get("nume_DB").toString()+","+data.get(2).get("user_DB").toString()+","+data.get(3).get("pass_DB").toString();
        return dateconectare;
    }
    synchronized public void deleteZeroValuesfromCos(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("cos","comandate = 0",null);
        database.close();
    }

    synchronized public void deleteItemfromCos(int cod){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] args = new String[]{Integer.toString(cod)};
        database.delete("cos","cod = ?",args);
        database.close();
    }


    synchronized public void setProdusComandat(int codProdus, int comanda){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("comandate", comanda);
        String[] args = new String[]{Integer.toString(codProdus)};
        Cursor cursor = database.rawQuery("SELECT * FROM cos WHERE cod = ?", args);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {

                database.update("cos", values, "cod=?", args);

        } else {
            values = new ContentValues();
            values.put("cod", codProdus);
            values.put("comandate", comanda);
            database.insert("cos",null, values);
        }
        database.close();
    }

    synchronized public int getNrProduse(Context ctx, Boolean all){
        ContentValues cv;
        String selectQuery;
        if(all) {
            selectQuery  = "SELECT * FROM produse";
        } else {
            selectQuery = "SELECT * FROM produse WHERE stoc <> 0";
        }
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int nrproduse = cursor.getCount();
        database.close();
        return nrproduse;
    }

    synchronized public List<ProduseValues> getProduse(Boolean all, String filtru, String clasa){
        List<ProduseValues> data = new ArrayList<ProduseValues>();
        ProduseValues pv;
        String selectQuery = "SELECT produse.cod AS cod, produse.denumire AS denumire, produse.um AS um, produse.stoc AS stoc, produse.rezervata AS rezervata, produse.tva AS tva, produse.pret_livr AS pret_livr, cos.comandate AS comandate FROM produse ";
        String selectCos = "LEFT OUTER JOIN cos ON produse.cod = cos.cod ";
        if(all && !filtru.isEmpty() && clasa.isEmpty()){
            selectQuery = selectQuery + selectCos + "WHERE denumire LIKE '%"+filtru+"%'";
        }
        if(!all && !filtru.isEmpty() && clasa.isEmpty()){
            selectQuery = selectQuery + selectCos + "WHERE stoc <> 0 AND denumire LIKE '%"+filtru+"%'";
        }
        if(all && filtru.isEmpty() && clasa.isEmpty()){
            selectQuery = selectQuery + selectCos;
        }
        if(!all && filtru.isEmpty() && clasa.isEmpty()){
            selectQuery = selectQuery + selectCos + "WHERE stoc <> 0";
        }
        if(all && !filtru.isEmpty() && !clasa.isEmpty()){
            selectQuery = selectQuery + selectCos + "WHERE denumire LIKE '%"+filtru+"%' AND clasa = '"+clasa+"'";
        }
        if(!all && !filtru.isEmpty() && !clasa.isEmpty()){
            selectQuery = selectQuery + selectCos + "WHERE stoc <> 0 AND denumire LIKE '%"+filtru+"%' AND clasa = '"+clasa+"'";
        }
        if(all && filtru.isEmpty() && !clasa.isEmpty()){
            selectQuery = selectQuery + selectCos + "WHERE clasa = '"+clasa+"'";
        }
        if(!all && filtru.isEmpty() && !clasa.isEmpty()){
            selectQuery = selectQuery + selectCos + "WHERE stoc <> 0 AND clasa = '"+clasa+"'";
        }

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            pv = new ProduseValues();
            pv.setDenumire(cursor.getString(cursor.getColumnIndex("denumire")));
            pv.setUm(cursor.getString(cursor.getColumnIndex("um")));
            pv.setCodProdus(cursor.getInt(cursor.getColumnIndex("cod")));
            pv.setStoc(cursor.getInt(cursor.getColumnIndex("stoc")));
            pv.setRezervata(cursor.getInt(cursor.getColumnIndex("rezervata")));
            pv.setTva(cursor.getInt(cursor.getColumnIndex("tva")));
            pv.setPret_livr(cursor.getFloat(cursor.getColumnIndex("pret_livr")));
            pv.setComandate(cursor.getInt(cursor.getColumnIndex("comandate")));
            data.add(pv);
            cursor.moveToNext();
        }
        database.close();
        return data;
    }

    synchronized public List<ProduseValues> getCos(){
        List<ProduseValues> data = new ArrayList<ProduseValues>();
        ProduseValues pv;
        String selectQuery = "SELECT cos.comandate AS comandate, cos.cod AS cod, produse.denumire AS denumire, produse.um AS um, produse.tva AS tva, produse.pret_livr AS pret_livr FROM cos ";
        String selectCos = "LEFT OUTER JOIN produse ON cos.cod = produse.cod ";
        selectQuery = selectQuery + selectCos;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            pv = new ProduseValues();
            pv.setDenumire(cursor.getString(cursor.getColumnIndex("denumire")));
            pv.setUm(cursor.getString(cursor.getColumnIndex("um")));
            pv.setCodProdus(cursor.getInt(cursor.getColumnIndex("cod")));
            pv.setTva(cursor.getInt(cursor.getColumnIndex("tva")));
            pv.setPret_livr(cursor.getFloat(cursor.getColumnIndex("pret_livr")));
            pv.setComandate(cursor.getInt(cursor.getColumnIndex("comandate")));
            data.add(pv);
            cursor.moveToNext();
        }
        database.close();
        return data;
    }


    synchronized public List<CategoriiValues> getCategoriiFiltrate(Boolean all, String filtru){
        List<CategoriiValues> data = new ArrayList<CategoriiValues>();
        CategoriiValues cv;
        String selectQuery = "SELECT clasa, COUNT(cod) AS buc FROM produse WHERE clasa <> GROUP BY clasa";
        if(all && !filtru.isEmpty()) {
            selectQuery = "SELECT clasa, COUNT(cod) AS buc FROM produse WHERE clasa LIKE '%"+filtru+"%' AND clasa <> '' GROUP BY clasa";
        }
        if(!all && !filtru.isEmpty()){
            selectQuery = "SELECT clasa, COUNT(cod) AS buc FROM produse WHERE stoc <> 0 AND clasa LIKE '%"+filtru+"%' AND CLASA <> '' GROUP BY clasa";
        }
        if(all && filtru.isEmpty()){
            selectQuery = "SELECT clasa, COUNT(cod) AS buc FROM produse WHERE clasa <> '' GROUP BY clasa";
        }
        if(!all && filtru.isEmpty()){
            selectQuery = "SELECT clasa, COUNT(cod) AS buc FROM produse WHERE stoc <> 0 AND clasa <> '' GROUP BY clasa";
        }

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            cv = new CategoriiValues();
            cv.setNumeCategorie(cursor.getString(cursor.getColumnIndex("clasa")));
            cv.setNrProduseInCategorie(cursor.getInt(cursor.getColumnIndex("buc")));
            data.add(cv);
            cursor.moveToNext();
        }
        database.close();
        return data;
    }
}
