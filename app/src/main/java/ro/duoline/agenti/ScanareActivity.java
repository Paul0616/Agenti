package ro.duoline.agenti;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

public class ScanareActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private final static int REQUEST_CAMERA = 10;
    private TextView txtCod, txtDenumire, txtUm, txtTva, txtPret_livr, txtStoc, txtRezervata, txtRamase;
    private Button scaneazaBtn;
    private static final int SCAN_LOADER_ID = 37;
    private static final int RESULT_SETTINGS = 20;
    private final static String SCAN_URL_BASE = "http://www.contliv.eu/agentiAplicatie";
    private final static String SCAN_FILE_PHP_QUERY = "scanareCod.php";
    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanare);
        txtCod = (TextView) findViewById(R.id.textViewCod);
        txtDenumire = (TextView) findViewById(R.id.textDenumire);
        txtUm = (TextView) findViewById(R.id.textUm);
        txtTva = (TextView) findViewById(R.id.textTva);
        txtPret_livr = (TextView) findViewById(R.id.textPret);
        txtStoc = (TextView) findViewById(R.id.textStoc);
        txtRezervata = (TextView) findViewById(R.id.textRezervate);
        txtRamase = (TextView) findViewById(R.id.Ramase);
        scaneazaBtn = (Button) findViewById(R.id.buttonScaneaza);

        scaneazaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PreferenceManager.getDefaultSharedPreferences(ScanareActivity.this).getString("introducereCod", "1").equals("1")) {
                    new IntentIntegrator(ScanareActivity.this).initiateScan();
                } else {
                    Toast.makeText(getBaseContext(), "trebuie implementat manual si de pe scanner", Toast.LENGTH_LONG).show(); //TODO:
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String cameraPermission = Manifest.permission.CAMERA;
        int permissionCheck = ContextCompat.checkSelfPermission(this, cameraPermission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestPermissions(new String[]{cameraPermission}, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // mBarcodeView.resume();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                txtCod.setText(result.getContents());
                String dateconectare = controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(ScanareActivity.this).getString("FIRMA", "Agenti"));
                String[] param = new String[3];
                param[0] = dateconectare;
                param[1] = PreferenceManager.getDefaultSharedPreferences(ScanareActivity.this).getString("DEBIT", "");
                param[2] = result.getContents();
                if(PreferenceManager.getDefaultSharedPreferences(ScanareActivity.this).getString("cautareCod", "1").equals("1")) {
                    makeURLConnection(makeURL(SCAN_URL_BASE, SCAN_FILE_PHP_QUERY, param), SCAN_LOADER_ID);
                } else {
                    Toast.makeText(getBaseContext(), "trebuie cautat pe telefon", Toast.LENGTH_LONG).show(); //TODO:
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        switch (requestCode) {
            case RESULT_SETTINGS:
               // showUserSettings();
                break;

        }
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
        String[] keys = new String[3];
        keys[0] = "dateconectare";
        keys[1] = "debit";
        keys[2] = "cod";
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
        try{
            if (loader.getId() == SCAN_LOADER_ID) {
                JSONArray jProduse = null;
                if (data != null) {
                    jProduse = new JSONArray(data);
                        if(jProduse.length() == 0){
                            Toast.makeText(getBaseContext(), "Codul scanat nu exista in baza de date", Toast.LENGTH_LONG).show();
                        }

                        for (int i = 0; i < jProduse.length(); i++) {
                            txtDenumire.setText(jProduse.getJSONObject(i).getString("denumire").toString());
                            txtUm.setText(jProduse.getJSONObject(i).getString("um").toString());
                            txtStoc.setText("" + jProduse.getJSONObject(i).getInt("stoc"));
                            txtRezervata.setText("" + jProduse.getJSONObject(i).getInt("rezervata"));
                            txtTva.setText(""+jProduse.getJSONObject(i).getInt("tva"));
                            txtPret_livr.setText(""+ jProduse.getJSONObject(i).getDouble("pret_livr"));
                            int ramase = jProduse.getJSONObject(i).getInt("stoc") - jProduse.getJSONObject(i).getInt("rezervata");
                            txtRamase.setText(""+ramase);
                        }

                } else {
                    Toast.makeText(getBaseContext(), "Verifica conexiunea de internet. Pentru verificare coduri este necesara...", Toast.LENGTH_LONG).show();
                }

            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;

        }

        return true;
    }
}
