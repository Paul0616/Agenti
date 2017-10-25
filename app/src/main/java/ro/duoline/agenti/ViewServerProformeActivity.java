package ro.duoline.agenti;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ViewServerProformeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private RecyclerView recyclerView;
    DBController controller = new DBController(this);
    private static final int LOADER_ID = 39;
    private final String FILE = "http://www.contliv.eu/agentiAplicatie/getProforme.php";
    private final static String DATE_CONECTARE = "dateconectare";
    private final static String IDUSER = "iduser";
    private List<Proformevalues> list = new ArrayList<Proformevalues>();
    private ProformeSalavateAdapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SaveSharedPreference.getStyle(this));
        setContentView(R.layout.activity_view_server_proforme);
        this.setTitle("Intocmite de: " + PreferenceManager.getDefaultSharedPreferences(ViewServerProformeActivity.this).getString("USER", ""));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerProformeServer);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        pd = new ProgressDialog(this);
        pd.setMessage("Se incarca lista cu proforme de pe server...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ContentValues cv = controller.getTotCont(PreferenceManager.getDefaultSharedPreferences(ViewServerProformeActivity.this).getString("USER", ""));
        String[] param = new String[]{
            controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(ViewServerProformeActivity.this).getString("FIRMA", "Agenti")),
             cv.getAsString("id_user")
        };
        makeURLConnection(makeURL(FILE,  param), LOADER_ID);

    }

    private void makeURLConnection(URL queryURL, int loaderID){
        Bundle queryBundle = new Bundle();
        queryBundle.putString("link",queryURL.toString());
        LoaderManager loaderManager = getSupportLoaderManager();
        android.support.v4.content.Loader<String> queryLoader = loaderManager.getLoader(loaderID);
        if(queryLoader == null){
            loaderManager.initLoader(loaderID, queryBundle, this);
        } else {
            loaderManager.restartLoader(loaderID, queryBundle, this);
        }
    }

    public URL makeURL(String base,  String[] parameters){
        Uri.Builder builder = new Uri.Builder();
            builder = Uri.parse(base).buildUpon();
            builder = builder.appendQueryParameter(DATE_CONECTARE, parameters[0]);
            builder = builder.appendQueryParameter(IDUSER, parameters[1]);
            Uri bultUri = builder.build();

        URL queryURL;
        try {
            queryURL = new URL(bultUri.toString());
            return queryURL;
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
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
                pd.show();
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
        JSONArray jArray = null;
        if (loader.getId() == LOADER_ID) {
            if (data != null) {
                try{
                    jArray = new JSONArray(data);
                    float total = 0;
                    int nr_unic = 0;
                    int nrcrt = 1;
                    String furn="", dataF="", cod_f ="";
                    int nrFact =0;
                    Proformevalues pv;
                    for(int i =0; i< jArray.length(); i++){
                        if(jArray.getJSONObject(i).getInt("nr_unic") != nr_unic){
                            if(total != 0){
                                pv = new Proformevalues();
                                pv.setCod_fiscal(cod_f);
                                pv.setClient(furn);
                                pv.setNrFact(nrFact);
                                pv.setData(dataF);
                                pv.setDenProdus("TOTAL:");
                                pv.setPret_livr(total);
                                pv.setParent(false);
                                pv.setVisible(false);
                                list.add(pv);
                            }
                            nr_unic = jArray.getJSONObject(i).getInt("nr_unic");
                            pv = new Proformevalues();
                            pv.setCod_fiscal(jArray.getJSONObject(i).getString("cod_fiscal"));
                            pv.setClient(jArray.getJSONObject(i).getString("furnizor"));
                            pv.setNrFact(jArray.getJSONObject(i).getInt("nr_fact"));
                            pv.setData(jArray.getJSONObject(i).getString("data"));
                            pv.setParent(true);
                            pv.setVisible(true);
                            list.add(pv);
                            nrcrt = 1;


                        }
                        pv = new Proformevalues();
                        pv.setBuc(jArray.getJSONObject(i).getInt("CANTI"));
                        pv.setDenProdus(jArray.getJSONObject(i).getString("denumire"));
                        pv.setUm(jArray.getJSONObject(i).getString("um"));
                        pv.setTva(jArray.getJSONObject(i).getInt("tva"));
                        pv.setPret_livr((float) jArray.getJSONObject(i).getDouble("pret_livr"));
                        pv.setNrCrt(nrcrt++);
                        pv.setCod_fiscal(jArray.getJSONObject(i).getString("cod_fiscal"));
                        pv.setClient(jArray.getJSONObject(i).getString("furnizor"));
                        pv.setNrFact(jArray.getJSONObject(i).getInt("nr_fact"));
                        pv.setData(jArray.getJSONObject(i).getString("data"));
                        pv.setParent(false);
                        pv.setVisible(false);
                        list.add(pv);
                        total = (float) jArray.getJSONObject(i).getDouble("rtotal");
                        furn = jArray.getJSONObject(i).getString("furnizor");
                        dataF = jArray.getJSONObject(i).getString("data");
                        cod_f = jArray.getJSONObject(i).getString("cod_fiscal");
                        nrFact = jArray.getJSONObject(i).getInt("nr_fact");
                        if(i == (jArray.length() - 1)){
                            pv = new Proformevalues();
                            pv.setCod_fiscal(cod_f);
                            pv.setClient(furn);
                            pv.setNrFact(nrFact);
                            pv.setData(dataF);
                            pv.setDenProdus("TOTAL:");
                            pv.setPret_livr(total);
                            pv.setParent(false);
                            pv.setVisible(false);
                            list.add(pv);
                        }
                    }
                    pd.dismiss();
                    adapter = new ProformeSalavateAdapter(getBaseContext(),list,ViewServerProformeActivity.this);
                    recyclerView.setAdapter(adapter);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "Verifica conexiunea de internet. Este necesara pentru obtinere numar proforma...",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
