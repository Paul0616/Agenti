package ro.duoline.agenti;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Calendar;

import java.util.HashMap;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;
    private ButoaneAdapter adapter;
    private static Context context;
    private long datestart;
    private String numeDbFirma, client, passw;
    //http://contliv.eu/agentiAplicatie/getProduse.php?dateconectare=192.168.88.5%2Ctixy_test%2CSYSDBA%2Cc%40ntliv2015&debit=371

    private AlertDialog alertDialog;
    private static final int FIRME_LOADER_ID = 33;
    private static final int USERI_LOADER_ID = 34;
    private static final int PRODUSE_LOADER_ID = 35;
    private static final int PARTENERI_LOADER_ID = 36;
    private final static String FIRME_URL_BASE = "http://www.contliv.eu/agentiAplicatie";
    private final static String FIRME_FILE_PHP_QUERY = "getFirme.php";
    private final static String USERI_FILE_PHP_QUERY = "getUseri.php";
    private final static String PRODUSE_FILE_PHP_QUERY = "getProduse.php";
    private final static String PARTENERI_FILE_PHP_QUERY = "getParteneri.php";
    private JSONArray jArray; //contine lista cu toate firmele si datele de conectare la Bazele lor de Date
    private Boolean produseLoaded = false;
    private Boolean parteneriLoaded = false;

    DBController controller = new DBController(this);

    private ProgressDialog pd;

    private ArrayList<ButoaneMeniuPrincipal>  butoane;
    public int temacurenta;

    @Override
    protected void onResume() {
        super.onResume();
        if(temacurenta != SaveSharedPreference.getStyle(this)){
            //setTheme(SaveSharedPreference.getStyle(this));
            recreate();
        }
        View parentLayout = findViewById(android.R.id.content);
        Long lastRefresh = SaveSharedPreference.getLastRefreshTime(context);
        lastRefresh = Calendar.getInstance().getTimeInMillis() - lastRefresh;
        Boolean needSync = SaveSharedPreference.getNeedSincronyze(MainActivity.this);
        if((lastRefresh > (30 * 60 * 1000)) || needSync){ //mai mare de 30 minute
            afiseazaSnackBar(parentLayout);
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        temacurenta = SaveSharedPreference.getStyle(this);
        setTheme(temacurenta);
        setContentView(R.layout.activity_main);
       // getApplication().setTheme(R.style.Dark);
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        butoane = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Se incarca stocurile si lista de clienti de pe server...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                afiseazaSnackBar(v);
            }
        });
        listCreate(context);
        adapter = new ButoaneAdapter(getApplicationContext(), butoane, this);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (!SaveSharedPreference.getLogged(this)){
            loadListaFirme();
            cereParola();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        } else {
            String title = "Firma: " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("FIRMA", "Agenti").toUpperCase();
            title = title + " User: " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("USER", "");
            this.setTitle(title);
        }


    }

    private void afiseazaSnackBar(View v){
        Long lastRefresh = SaveSharedPreference.getLastRefreshTime(context);
        String refreshTxt = "";
        if(lastRefresh == 0){
            refreshTxt = "Nu a fost facut nici un Refresh pentru stocuri";
        } else {
            lastRefresh = Calendar.getInstance().getTimeInMillis() - lastRefresh;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(lastRefresh);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(lastRefresh);
            long hours = TimeUnit.MILLISECONDS.toHours(lastRefresh);
            refreshTxt = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(lastRefresh),
                    TimeUnit.MILLISECONDS.toMinutes(lastRefresh) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(lastRefresh)),
                    TimeUnit.MILLISECONDS.toSeconds(lastRefresh) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lastRefresh)));
            refreshTxt = refreshTxt + " de de la ultima incarcare a stocurilor in telefon";
        }
        Snackbar.make(v, refreshTxt, Snackbar.LENGTH_LONG)
                .setAction("SINCRONIZEAZA", new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String dateconectare = controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("FIRMA", "Agenti"));
                        produseLoaded = false;
                        parteneriLoaded = false;
                        loadProduse(dateconectare, PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("DEBIT", ""));
                    }
                }).show();
        SaveSharedPreference.setNeedSincronyze(MainActivity.this, false);
    }

    //*****************functie pentru afisare introducerii parolei**************
    public void cereParola(){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.cere_parola, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText numeFirma = (EditText) promptsView.findViewById(R.id.editTextNume);
        final EditText user = (EditText) promptsView.findViewById(R.id.editTextUser);
        final EditText parola = (EditText) promptsView.findViewById(R.id.editTextParola);
        user.setEnabled(false);
        parola.setEnabled(false);

        numeFirma.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(controller.isFirmaInDB(s.toString())) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    String dateconectare = controller.getDateConectare(s.toString());
                    String[] param = new String[1];
                    param[0] = dateconectare;
                    URL url = makeURL(FIRME_URL_BASE, USERI_FILE_PHP_QUERY, param);
                    loadAcces(dateconectare);
                    //user.setEnabled(true);
                    //parola.setEnabled(true);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    numeFirma.setError("Firma nu exista");
                    user.setEnabled(false);
                    parola.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //set dialog message
        alertDialogBuilder.setCancelable(false)
               .setPositiveButton("OK", null);

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();


        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                numeDbFirma = numeFirma.getText().toString();
                client = user.getText().toString();
                passw = parola.getText().toString();

                if(controller.isUserValid(client, passw)){
                    alertDialog.dismiss();
                    SaveSharedPreference.setLoggedIn(MainActivity.this);
                    SaveSharedPreference.setFirma(MainActivity.this, numeDbFirma);
                    ContentValues cv = controller.getDebit(client, passw);
                    SaveSharedPreference.setDebit(MainActivity.this, cv.get("debit").toString());
                    SaveSharedPreference.setUser(MainActivity.this, client);
                    String title = "Firma: " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("FIRMA", "Agenti").toUpperCase();
                    title = title + " User: " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("USER", "");
                    MainActivity.this.setTitle(title);
                } else {
                        user.setError("User name gresit");
                        parola.setError("Parola gresita");
                }
            }
        });


    }
    private void tes(){
        alertDialog.findViewById(R.id.editTextUser).setEnabled(true);
        alertDialog.findViewById(R.id.editTextParola).setEnabled(true);
    }

    //*****************************************************************


    public void loadListaFirme(){
        makeURLConnection(makeURL(FIRME_URL_BASE, FIRME_FILE_PHP_QUERY, null), FIRME_LOADER_ID);

    }

    public void loadProduse(String dateconectare, String contGestiune){
        String[] param = new String[2];
        param[0] = dateconectare;
        param[1] = contGestiune;
        SaveSharedPreference.setLastRefreshTime(context, Calendar.getInstance().getTimeInMillis());
        makeURLConnection(makeURL(FIRME_URL_BASE, PRODUSE_FILE_PHP_QUERY, param), PRODUSE_LOADER_ID);

    }

    public void loadParteneri(){
        String dateconectare = controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("FIRMA", "Agenti"));
        String[] param = new String[1];
        param[0] = dateconectare;
        makeURLConnection(makeURL(FIRME_URL_BASE, PARTENERI_FILE_PHP_QUERY, param), PARTENERI_LOADER_ID);

    }

    public void loadAcces(String dateconectare){
        String[] param = new String[1];
        param[0] = dateconectare;
        makeURLConnection(makeURL(FIRME_URL_BASE, USERI_FILE_PHP_QUERY, param), USERI_LOADER_ID);

    }

    private void makeURLConnection(URL queryURL, int loaderID){
        Bundle queryBundle = new Bundle();
        queryBundle.putString("link",queryURL.toString());
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> queryLoader = loaderManager.getLoader(loaderID);
        if(queryLoader == null){
            loaderManager.initLoader(loaderID, queryBundle, this);
        } else {
            loaderManager.restartLoader(loaderID, queryBundle, this);
        }
    }

    private URL makeURL(String base, String file, String[] parameters){
        Uri.Builder builder = new Uri.Builder();
        Uri bultUri;
        //bultUri = Uri.withAppendedPath(bultUri, file);
        String[] keys = new String[2];
        keys[0] = "dateconectare";
        keys[1] = "debit";
        if (parameters != null){
            builder = Uri.parse(base).buildUpon().appendPath(file);
            for(int i = 0; i < parameters.length; i++) {
                builder = builder.appendQueryParameter(keys[i], parameters[i]);
            }
            bultUri = builder.build();
        } else {
            bultUri = Uri.parse(base).buildUpon().appendPath(file).build();
        }
        URL queryURL;
        try {
            queryURL = new URL(bultUri.toString());
            return queryURL;
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    private void listCreate(Context context){
        ButoaneMeniuPrincipal btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(ContextCompat.getColor(context, R.color.colorComanda));
        btn.setTextButon("COMANDA");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_name));
        btn.setMesaj(false);
        butoane.add(btn);
/*
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(ContextCompat.getColor(context, R.color.colorScanare));
        btn.setTextButon("SCANARE / INVENTARIERE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        btn.setMesaj(false);
        butoane.add(btn);
       */
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(ContextCompat.getColor(context, R.color.colorSalvate));
        btn.setTextButon("PROFORME SALVATE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_salvate));
        btn.setMesaj(controller.isCosNetrimis());

        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(ContextCompat.getColor(context, R.color.colorProforme));
        btn.setTextButon("PROFORME");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_proforme));
        btn.setMesaj(false);
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(ContextCompat.getColor(context, R.color.colorFacturi));
        btn.setTextButon("FACTURI");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_facturi));
        btn.setMesaj(false);
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(ContextCompat.getColor(context, R.color.colorMemo));
        btn.setTextButon("OPTIUNI");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_memo));
        btn.setMesaj(false);
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(ContextCompat.getColor(context, R.color.colorDeconectare));
        btn.setTextButon("DECONECTARE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_deconectare));
        btn.setMesaj(false);
        butoane.add(btn);
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }    catch (IOException e) {
            return null;
        }    finally

         {
            urlConnection.disconnect();
        }
    }

    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(id == PRODUSE_LOADER_ID) {
                    datestart = Calendar.getInstance().getTimeInMillis();

                    pd.show();
                }
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String queryURLString = args.getString("link");
                if(queryURLString == null || queryURLString == "") return null;
                try{
                    URL queryURL = new URL(queryURLString);
                    String result = getResponseFromHttpUrl(queryURL);

                    return result;
                } catch (IOException e){

                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        try {
            if (loader.getId() == FIRME_LOADER_ID) {
                jArray = null;
                if (data != null) {
                    jArray = new JSONArray(data);
                    // String test = jArray.getJSONObject(11).getString("firma").toString();
                    //Toast.makeText(context,test,Toast.LENGTH_SHORT).show();
                    controller.deleteAllRecords("db_list");
                    for (int i = 0; i < jArray.length(); i++){
                        HashMap<String, String> queryValues = new HashMap<String, String>();
                        queryValues.put("firma", jArray.getJSONObject(i).getString("firma").toString());
                        queryValues.put("ip", jArray.getJSONObject(i).getString("ip").toString());
                        queryValues.put("nume_DB", jArray.getJSONObject(i).getString("nume_db").toString());
                        queryValues.put("user_DB", jArray.getJSONObject(i).getString("user_db").toString());
                        queryValues.put("pass_DB", jArray.getJSONObject(i).getString("pass_db").toString());
                        controller.insertFirme(queryValues);
                    }

                } else {
                    Toast.makeText(context, "Verifica conexiunea de internet. Pentru logare este necesara...",Toast.LENGTH_LONG).show();
                }
            }
            if (loader.getId() == USERI_LOADER_ID) {
                jArray = null;
                if (data != null) {
                    jArray = new JSONArray(data);
                    // String test = jArray.getJSONObject(11).getString("firma").toString();
                    //Toast.makeText(context,test,Toast.LENGTH_SHORT).show();
                    controller.deleteAllRecords("acces");
                    for (int i = 0; i < jArray.length(); i++) {
                        HashMap<String, String> queryValuesString = new HashMap<String, String>();
                        queryValuesString.put("user", jArray.getJSONObject(i).getString("user").toString());
                        queryValuesString.put("parola", jArray.getJSONObject(i).getString("parola").toString());
                        queryValuesString.put("cod_gestiune", jArray.getJSONObject(i).getString("cod_gestiune").toString());
                        queryValuesString.put("nume_gestiune", jArray.getJSONObject(i).getString("nume_gestiune").toString());
                        queryValuesString.put("debit", jArray.getJSONObject(i).getString("debit").toString());

                        HashMap<String, Integer> queryValuesInt = new HashMap<String, Integer>();
                        queryValuesInt.put("id_gestiune", jArray.getJSONObject(i).getInt("id_gestiune"));
                        queryValuesInt.put("nr_gestiune", jArray.getJSONObject(i).getInt("nr_gestiune"));
                        queryValuesInt.put("pozitie_pret", jArray.getJSONObject(i).getInt("pozitie_pret"));
                        queryValuesInt.put("nr_proforme", jArray.getJSONObject(i).getInt("nr_proforme"));
                        queryValuesInt.put("nr_proformef", jArray.getJSONObject(i).getInt("nr_proformef"));
                        controller.insertUseri(queryValuesString, queryValuesInt);
                    }
                    tes();
                } else {
                    Toast.makeText(context, "Verifica conexiunea de internet. Pentru logare este necesara...", Toast.LENGTH_LONG).show();
                }
            }

            if (loader.getId() == PRODUSE_LOADER_ID) {
                JSONArray jProduse = null;
                 if (data != null) {
                     jProduse = new JSONArray(data);
                     controller.deleteAllRecords("produse");

                     SQLiteDatabase db = controller.getWritableDatabase();
                     db.beginTransaction();
                     try {
                         for (int i = 0; i < jProduse.length(); i++) {
                             HashMap<String, String> queryValuesString = new HashMap<String, String>();
                             queryValuesString.put("clasa", jProduse.getJSONObject(i).getString("clasa").toString());
                             queryValuesString.put("denumire", jProduse.getJSONObject(i).getString("denumire").toString());
                             queryValuesString.put("um", jProduse.getJSONObject(i).getString("um").toString());

                             HashMap<String, Integer> queryValuesInt = new HashMap<String, Integer>();
                             queryValuesInt.put("cod", jProduse.getJSONObject(i).getInt("cod"));
                             queryValuesInt.put("stoc", jProduse.getJSONObject(i).getInt("stoc"));
                             queryValuesInt.put("rezervata", jProduse.getJSONObject(i).getInt("rezervata"));
                             queryValuesInt.put("tva", jProduse.getJSONObject(i).getInt("tva"));

                             HashMap<String, Double> queryValuesFloat = new HashMap<String, Double>();
                             queryValuesFloat.put("pret_livr", jProduse.getJSONObject(i).getDouble("pret_livr"));
                             //pd.setProgress(i);

                             controller.insertProduse(queryValuesString, queryValuesInt, queryValuesFloat, db);

                         }
                         db.setTransactionSuccessful();
                     } finally {
                         db.endTransaction();
                         produseLoaded = true;
                     }
                     db.close();

                 } else {
                     Toast.makeText(context, "Verifica conexiunea de internet. Pentru actualizare stocuri este necesara...", Toast.LENGTH_LONG).show();
                 }

            }

            if (loader.getId() == PARTENERI_LOADER_ID) {
                JSONArray jProduse = null;
                if (data != null) {
                    jProduse = new JSONArray(data);
                    controller.deleteAllRecords("parteneri");

                    SQLiteDatabase db = controller.getWritableDatabase();
                    db.beginTransaction();
                    try {
                        for (int i = 0; i < jProduse.length(); i++) {
                            HashMap<String, String> queryValuesString = new HashMap<String, String>();
                            queryValuesString.put("denumire", jProduse.getJSONObject(i).getString("denumire").toString());
                            queryValuesString.put("codtara", jProduse.getJSONObject(i).getString("codtara").toString());
                            queryValuesString.put("cod_fiscal", jProduse.getJSONObject(i).getString("cod_fiscal"));


                            controller.insertParteneri(queryValuesString, db);

                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                        parteneriLoaded = true;
                    }
                    db.close();

                } else {
                    Toast.makeText(context, "Verifica conexiunea de internet. Pentru actualizare clienti este necesara...", Toast.LENGTH_LONG).show();
                }

            }
            if(produseLoaded){
                loadParteneri();
            }
            if(parteneriLoaded && produseLoaded) {
                long milis = Calendar.getInstance().getTimeInMillis() - datestart;
                pd.dismiss();
                Toast.makeText(context, "Baza de date actualizata in " + Long.toString(milis) + "ms", Toast.LENGTH_LONG).show();
                parteneriLoaded = false;
                produseLoaded = false;
            }

        }catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
