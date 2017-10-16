package ro.duoline.agenti;




import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Date;

import java.util.List;
import java.util.Scanner;

//import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ProformaView extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private TextView nrProforma, dataProforma, totalProforma, clientProforma;
    private RecyclerView recyclerViewProforma;
    private Button mEmiteButon;
    private LinearLayoutManager layoutManager;
    DBController controller = new DBController(this);
    private CosAdapter adapter;
    private List<ProduseValues> listaCos;
    private final static String FIRME_URL_BASE = "http://www.contliv.eu/agentiAplicatie";
    private final static String NRPROFORMA_FILE_PHP_QUERY = "getNrProforma.php";
    private static final int NRPROFORMA_LOADER_ID = 37;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proforma_view);
        nrProforma = (TextView) findViewById(R.id.nrProformaTextView);
        dataProforma = (TextView) findViewById(R.id.dataProformaTextView);
        totalProforma = (TextView) findViewById(R.id.totalProformaTextView);
        clientProforma = (TextView) findViewById(R.id.clientTextView);
        recyclerViewProforma = (RecyclerView) findViewById(R.id.recyclerViewproforma);
        mEmiteButon = (Button) findViewById(R.id.EmiteProdormaButton);
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        dataProforma.setText(sdf.format(new Date()));
        String[] client = controller.getClientFromCos();
        clientProforma.setText(client[0]);
        listaCos = controller.getCos();
        adapter = new CosAdapter(this, listaCos, controller, this, 2);
        recyclerViewProforma.setAdapter(adapter);
        setTotal();
        String dateconectare = controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(ProformaView.this).getString("FIRMA", "Agenti"));
        String[] param = new String[3];
        param[0] = dateconectare;
        param[1] = "3000000";
        param[2] = "3999999";
        makeURLConnection(makeURL(FIRME_URL_BASE, NRPROFORMA_FILE_PHP_QUERY, param), NRPROFORMA_LOADER_ID);
        mEmiteButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDataForUpload();
            }
        });
    }

    private void prepareDataForUpload(){
        ContentValues cv = controller.getTotCont(PreferenceManager.getDefaultSharedPreferences(ProformaView.this).getString("USER", ""));
         //tot cont + gest
        List<ProduseValues> cos = controller.getCos();
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
        String[] client = controller.getClientFromCos();
        Toast.makeText(getBaseContext(), cv.get("totcontul").toString() + "\n" + cv.get("gest").toString() + "\n" + cv.get("id_user").toString() + "\n" + client[0] + "\n" + client[1] + "\n" +
                Float.toString(rTotal) + "\n" + Float.toString(rTva) + "\n" + Float.toString(rValoare), Toast.LENGTH_LONG).show();
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

    private URL makeURL(String base, String file, String[] parameters){
        Uri.Builder builder = new Uri.Builder();
        Uri bultUri;
        //bultUri = Uri.withAppendedPath(bultUri, file);
        String[] keys = new String[3];
        keys[0] = "dateconectare";
        keys[1] = "NR_PROFORME";
        keys[2] = "NR_PROFORMEF";
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
                Toast.makeText(getBaseContext(), "Verifica conexiunea de internet. Pentru logare este necesara...",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

