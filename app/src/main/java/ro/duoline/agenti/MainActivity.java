package ro.duoline.agenti;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ButoaneAdapter adapter;
    private static Context context;
    private String numeDbFirma, client, passw;
    //http://contliv.eu/agentiAplicatie/getProduse.php?dateconectare=192.168.88.5%2Ctixy_test%2CSYSDBA%2Cc%40ntliv2015&debit=371

    private AlertDialog alertDialog;
    private static final int FIRME_LOADER_ID = 33;
    private static final int USERI_LOADER_ID = 34;
    private final static String FIRME_URL_BASE = "http://www.contliv.eu/agentiAplicatie";
    private final static String FIRME_FILE_PHP_QUERY = "getFirme.php";
    private final static String USERI_FILE_PHP_QUERY = "getUseri.php";
    private JSONArray jArray; //contine lista cu toate firmele si datele de conectare la Bazele lor de Date
    DBController controller = new DBController(this);

    private ArrayList<ButoaneMeniuPrincipal>  butoane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        butoane = new ArrayList<>();
        listCreate(context);
        adapter = new ButoaneAdapter(getApplicationContext(), butoane);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        loadListaFirme();
        if (!SaveSharedPreference.getLogged(this)){
            cereParola();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        } else {
            String title = "Firma: " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("FIRMA", "Agenti").toUpperCase();
            title = title + " User: " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("USER", "");
            this.setTitle(title);
        }


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
                    List<ContentValues> date = controller.getDateConectare(s.toString());
                    String dateconectare = date.get(0).get("ip").toString()+","+date.get(1).get("nume_DB").toString()+","+date.get(2).get("user_DB").toString()+","+date.get(3).get("pass_DB").toString();
                    URL url = makeURL(FIRME_URL_BASE, USERI_FILE_PHP_QUERY, dateconectare);
                    makeURLConnection(makeURL(FIRME_URL_BASE, USERI_FILE_PHP_QUERY, dateconectare), USERI_LOADER_ID);
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
        makeURLConnection(makeURL(FIRME_URL_BASE, FIRME_FILE_PHP_QUERY, ""), FIRME_LOADER_ID);

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

    private URL makeURL(String base, String file, String parameters){
        Uri bultUri;
        //bultUri = Uri.withAppendedPath(bultUri, file);
        if (!parameters.isEmpty()){
            bultUri = Uri.parse(base).buildUpon().appendPath(file).appendQueryParameter("dateconectare", parameters).build();
        } else {
            bultUri = Uri.parse(base).buildUpon().appendPath(file).build();
        }
        URL queryURL = null;
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
        btn.setCuloareButon(context.getColor(R.color.colorComanda));
        btn.setTextButon("COMANDA");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_name));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorScanare));
        btn.setTextButon("SCANARE / INVENTARIERE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorSalvate));
        btn.setTextButon("PROFORME SALVATE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorProforme));
        btn.setTextButon("PROFORME");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorFacturi));
        btn.setTextButon("FACTURI");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorMemo));
        btn.setTextButon("MEMO");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorDeconectare));
        btn.setTextButon("DECONECTARE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
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
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
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
                        controller.insertUseri(queryValuesString, queryValuesInt);
                    }
                    tes();
                } else {
                    Toast.makeText(context, "Verifica conexiunea de internet. Pentru logare este necesara...", Toast.LENGTH_LONG).show();
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
