package ro.duoline.agenti;




import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.net.URLEncoder;
import java.util.Date;

import java.util.List;
import java.util.Scanner;

//import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ProformaViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private TextView nrProforma, dataProforma, totalProforma, clientProforma;
    private RecyclerView recyclerViewProforma;
    private Button mEmiteButon, mSalveaza;
    private LinearLayoutManager layoutManager;
    DBController controller = new DBController(this);
    private CosAdapter adapter;
    private List<ProduseValues> listaCos;
    private final static String FIRME_URL_BASE = "http://www.contliv.eu/agentiAplicatie";
    private final static String NRPROFORMA_FILE_PHP_QUERY = "getNrProforma.php";
    private final static String SETPROFORMA_FILE_PHP_QUERY = "setProforma.php";
    private static final int NRPROFORMA_LOADER_ID = 37;
    private static final int SETPROFORMA_LOADER_ID = 38;
    private static String COD_FISCAL;
    private static String DATA;
    private static long NRPROVIZORIU;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SaveSharedPreference.getStyle(this));
        setContentView(R.layout.activity_proforma_view);
        nrProforma = (TextView) findViewById(R.id.nrProformaTextView);
        dataProforma = (TextView) findViewById(R.id.dataProformaTextView);
        totalProforma = (TextView) findViewById(R.id.totalProformaTextView);
        clientProforma = (TextView) findViewById(R.id.clientTextView);
        recyclerViewProforma = (RecyclerView) findViewById(R.id.recyclerViewproforma);
        mEmiteButon = (Button) findViewById(R.id.EmiteProdormaButton);
        mSalveaza = (Button) findViewById(R.id.salveaza_button);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewProforma.setLayoutManager(layoutManager);
        int iColor = Color.parseColor("#cdcdcd");
        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };
        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        recyclerViewProforma.addItemDecoration(new LineItemDecoration(this, colorFilter));

        Intent intent = getIntent();
        COD_FISCAL = intent.getStringExtra("cod_fiscal");
        DATA = intent.getStringExtra("data");
        NRPROVIZORIU = intent.getLongExtra("nrProvizoriu", 0);
        if(COD_FISCAL == null && DATA == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            dataProforma.setText(sdf.format(new Date()));
            String[] client = controller.getClientFromCos();
            clientProforma.setText(client[0]);
            listaCos = controller.getCos();
        } else {
            dataProforma.setText(DATA);
            clientProforma.setText(controller.getClientFromParteneri(COD_FISCAL));
            listaCos = controller.getCosSalvat(COD_FISCAL, DATA, NRPROVIZORIU);
        }



        adapter = new CosAdapter(this, listaCos, controller, this, 2);
        recyclerViewProforma.setAdapter(adapter);
        setTotal();
        String dateconectare = controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(ProformaViewActivity.this).getString("FIRMA", "Agenti"));
        String[] param = new String[3];
        param[0] = dateconectare;
        Integer[] proforme = controller.nrProforme(PreferenceManager.getDefaultSharedPreferences(ProformaViewActivity.this).getString("USER", ""));
        param[1] = proforme[0].toString();
        param[2] = proforme[1].toString();
        makeURLConnection(makeURL(FIRME_URL_BASE, NRPROFORMA_FILE_PHP_QUERY, param), NRPROFORMA_LOADER_ID);
        mEmiteButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute();
            }
        });
        mSalveaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(COD_FISCAL == null && DATA == null) {
                    controller.setCosNetrimis();
                }

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    private JSONArray prepareDataForUpload(){
        ContentValues cv = controller.getTotCont(PreferenceManager.getDefaultSharedPreferences(ProformaViewActivity.this).getString("USER", ""));
         //tot cont + gest
        List<ProduseValues> cos;
        if(COD_FISCAL == null && DATA == null) {
            cos = controller.getCos();
        } else {
            cos = controller.getCosSalvat(COD_FISCAL, DATA, NRPROVIZORIU);
        }
        float rTotal = 0;
        float rTva = 0;
        float rValoare = 0;
        float discount = 0;
        float taxaeco = 0;
        for(int i =0; i <cos.size(); i++){
            Float reduceri = round2((cos.get(i).getPret_livr() - taxaeco) * discount / 100, 3);
            rTotal = rTotal + round2(cos.get(i).getComandate() * (cos.get(i).getPret_livr() - reduceri), 2);
            Float subtotal = round2(cos.get(i).getComandate()*(cos.get(i).getPret_livr()-reduceri),2);

            Float subtotalFaraTva = round2(subtotal * 100 / (100 + cos.get(i).getTva()),2);

            rTva = round2(rTva + subtotal - subtotalFaraTva, 2);
            int x = 100 + cos.get(i).getTva();
        }
        rValoare = round2(rTotal - rTva, 2);
        String[] client;
        if(COD_FISCAL == null && DATA == null) {
            client = controller.getClientFromCos();//client[0] = numele clientului si client[1] = codul fiscal
        } else {
            client = new String[]{controller.getClientFromParteneri(COD_FISCAL),COD_FISCAL};
        }
        Integer[] proforme = controller.nrProforme(PreferenceManager.getDefaultSharedPreferences(ProformaViewActivity.this).getString("USER", ""));
        try {
            JSONArray jarrFinal = new JSONArray();
            JSONObject jo = new JSONObject();
            jo.accumulate("nr_proforme", proforme[0]);
            jo.accumulate("nr_proformef", proforme[1]);
            jo.accumulate("gest", cv.get("gest").toString());
            jo.accumulate("totcontul", cv.get("totcontul").toString());
            jo.accumulate("cod_fiscal", client[1]);
            jo.accumulate("furnizor", client[0]);
            jo.accumulate("rtva", rTva);
            jo.accumulate("rTotal", rTotal);
            jo.accumulate("rValoare", rValoare);
            jo.accumulate("id_user", cv.get("id_user"));
            jarrFinal.put(jo);
            JSONObject jo1 = new JSONObject();
            for(int i = 0; i < listaCos.size(); i++) {
                jo1.accumulate("cod", listaCos.get(i).getCodProdus());
                jo1.accumulate("um", listaCos.get(i).getUm());
                jo1.accumulate("denumire", listaCos.get(i).getDenumire());
                jo1.accumulate("qty", listaCos.get(i).getComandate());
                jo1.accumulate("pret", listaCos.get(i).getPret_livr());
                jo1.accumulate("tva", listaCos.get(i).getTva());
                jo1.accumulate("debit", PreferenceManager.getDefaultSharedPreferences(ProformaViewActivity.this).getString("DEBIT", ""));
                jo1.accumulate("gest", cv.get("gest").toString());
            }
            jarrFinal.put(jo1);

            return jarrFinal;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }
    public void pregatesteButoaneSalvare(){
        mSalveaza.setEnabled(true);
        mEmiteButon.setEnabled(false);
        Snackbar snack = Snackbar.make(getCurrentFocus(), "Trimitere esuata. Poti salva PROFORMA si s-o trimiti mai tarziu.", Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }

    public void setTotal(){
        totalProforma.setText("Total: "+Float.toString(calculTotal())+" lei");
    }
    public float calculTotal() {
        float res = 0;
        for(int i=0; i<listaCos.size(); i++){
            res += (listaCos.get(i).getComandate() * listaCos.get(i).getPret_livr());
        }
        return round2(res, 2);
    }

    public float round2(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
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

    public URL makeURL(String base, String file, String[] parameters){
        Uri.Builder builder = new Uri.Builder();
        Uri bultUri;
        //bultUri = Uri.withAppendedPath(bultUri, file);
        String[] keys = new String[5];
        keys[0] = "dateconectare";
        keys[1] = "NR_PROFORME";
        keys[2] = "NR_PROFORMEF";
        keys[3] = "dateconectare";
        keys[4] = "sirjson";

        if (parameters != null){
            builder = Uri.parse(base).buildUpon().appendPath(file);
            for(int i = 0; i < parameters.length; i++) {
                if(parameters.length == 2) {
                    builder = builder.appendQueryParameter(keys[i+3], parameters[i]);
                } else {
                    builder = builder.appendQueryParameter(keys[i], parameters[i]);
                }
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

        if (loader.getId() == NRPROFORMA_LOADER_ID) {
            if (data != null) {
                nrProforma.setText("PROFORMA nr. " + data);
            } else {
                Toast.makeText(getBaseContext(), "Verifica conexiunea de internet. Este necesara pentru obtinere numar proforma...",Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }



    private class HttpAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            JSONArray proforma = prepareDataForUpload();
            String dateconectare = controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(ProformaViewActivity.this).getString("FIRMA", "Agenti"));
            try{
                String json = URLEncoder.encode(proforma.toString(), "UTF-8");
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. initialize HTTPGet request
                HttpGet request = new HttpGet();


                request.setURI(new URI(FIRME_URL_BASE+"/"+SETPROFORMA_FILE_PHP_QUERY+"?sirjson="+json+"&dateconectare="+dateconectare));
                // 2. Execute GET request to the given URL
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();


                    Scanner scanner = new Scanner(is);
                    scanner.useDelimiter("\\A");

                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        return scanner.next();
                    } else {
                        return "{\"error\":true, \"message\":\"Serverul nu raspunde\"}";
                    }



                //return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"error\":true, \"message\":\"Lispa Internet\"}";

            }


        }

        @Override
        protected void onPostExecute(String s) {
            //Mesaj comanda trimisa
            try {
                JSONObject jsonObj = new JSONObject(s);
                if(jsonObj != null && !jsonObj.getBoolean("error")) {
                    JSONArray proforma = prepareDataForUpload();
                    String cod_fiscalDeLaServer = jsonObj.getJSONArray("factura").getJSONObject(0).getString("cod_fiscal");
                    if(proforma.getJSONObject(0).getString("cod_fiscal").equals(cod_fiscalDeLaServer)) {
                        if (COD_FISCAL == null && DATA == null) {
                            controller.deletefromCosAllTrimise();
                        } else {
                            controller.deletefromCosSalvate(COD_FISCAL, DATA, NRPROVIZORIU);
                        }
                        SaveSharedPreference.setNeedSincronyze(ProformaViewActivity.this, true);
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(i);
                        Toast.makeText(getBaseContext(), "PROFORMA TRIMISA CU SUCCES!!!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ProformaViewActivity.this, "Eroare de salvare pe server:\n" + jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                    pregatesteButoaneSalvare();
                }

            } catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

}

