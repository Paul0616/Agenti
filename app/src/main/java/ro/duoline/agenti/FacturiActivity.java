package ro.duoline.agenti;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

public class FacturiActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private RecyclerView recyclerView;
    DBController controller = new DBController(this);
    private static final int LOADER_ID = 40;
    private final String FILE = "http://www.contliv.eu/agentiAplicatie/getFacturi.php";
    private final static String DATE_CONECTARE = "dateconectare";
    private final static String IDUSER = "iduser";
    private final static String DELA = "dela";
    private final static String PANALA = "panala";
    private List<Proformevalues> list = new ArrayList<Proformevalues>();
   // private FacturiAdapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    private ProgressDialog pd;
    private ImageView iconDeLa, iconPanaLa;
    private DatePickerDialog datePickerDialog;
    private TextView textDeLA, textPanaLa;
    private String delaFomat, panalaFormat;
    private Button getFacturiBtn;
    private ProformeSalavateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SaveSharedPreference.getStyle(this));
        setContentView(R.layout.activity_facturi);
        this.setTitle("Facturi: " + PreferenceManager.getDefaultSharedPreferences(FacturiActivity.this).getString("USER", ""));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerFacturiServer);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        iconDeLa = (ImageView) findViewById(R.id.imageViewcalendarDeLa);
        iconPanaLa = (ImageView) findViewById(R.id.imageViewCalendarPanaLa);
        textDeLA = (TextView) findViewById(R.id.textViewDeLa);
        textPanaLa = (TextView) findViewById(R.id.textViewPanaLa);
        getFacturiBtn = (Button) findViewById(R.id.buttonGetFacturi);
        pd = new ProgressDialog(this);
        pd.setMessage("Se incarca lista cu facturi de pe server...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);


      //  SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");
        Calendar currentDate = Calendar.getInstance();
        String data2 = currentDate.get(Calendar.DATE) + "." + (currentDate.get(Calendar.MONTH)+1) + "." + currentDate.get(Calendar.YEAR);

        textPanaLa.setText(data2);
        panalaFormat = setDateInServerFormat(currentDate);
     //   try {
            Calendar firstdate = new GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1);
            String data1 = firstdate.get(Calendar.DATE) + "." + (firstdate.get(Calendar.MONTH)+1) + "." + firstdate.get(Calendar.YEAR);
            textDeLA.setText(data1);
            delaFomat = setDateInServerFormat(firstdate);
     //   } catch (Exception e){
     //       e.printStackTrace();
     //   }

        getFacturiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = controller.getTotCont(PreferenceManager.getDefaultSharedPreferences(FacturiActivity.this).getString("USER", ""));
                String[] param = new String[]{
                        controller.getDateConectare(PreferenceManager.getDefaultSharedPreferences(FacturiActivity.this).getString("FIRMA", "Agenti")),
                        cv.getAsString("id_user"),
                        delaFomat,
                        panalaFormat
                };
                makeURLConnection(makeURL(FILE,  param), LOADER_ID);
            }
        });

        iconDeLa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date da = df.parse(textDeLA.getText().toString());
                    c.setTime(da);
                } catch (Exception e){
                    e.printStackTrace();
                }

                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                datePickerDialog = new DatePickerDialog(FacturiActivity.this, new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        textDeLA.setText(dayOfMonth + "." + (month + 1) + "." + year);
                        Calendar date = new GregorianCalendar(year, (month), dayOfMonth);
                        delaFomat = setDateInServerFormat(date);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
            }
        });

        iconPanaLa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date da = df.parse(textPanaLa.getText().toString());
                    c.setTime(da);
                } catch (Exception e){
                    e.printStackTrace();
                }
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(FacturiActivity.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textPanaLa.setText(dayOfMonth + "." + (month + 1) + "." + year);
                        Calendar date = new GregorianCalendar(year, (month), dayOfMonth);
                        panalaFormat = setDateInServerFormat(date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
            }
        });


    }
    private String setDateInServerFormat(Calendar date){
       String dataFormatata = "";
        int y = date.get(Calendar.YEAR);
        int m = date.get(Calendar.MONTH);
        int d = date.get(Calendar.DATE);
        Calendar currentdate = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
        dataFormatata = (currentdate.get(Calendar.MONTH) + 1)  + "-" + currentdate.get(Calendar.DATE) + "-" + currentdate.get(Calendar.YEAR);
       return dataFormatata;
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
        builder = builder.appendQueryParameter(DELA, parameters[2]);
        builder = builder.appendQueryParameter(PANALA, parameters[3]);
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
                list.clear();
                try {
                    jArray = new JSONArray(data);
                    float total = 0;
                    long nr_unic = 0;
                    int nrcrt = 1;
                    String furn = "", dataF = "", cod_f = "";
                    long nr_unic_vechi = 0;
                    int nrFact = 0;
                    Proformevalues pv;
                    for (int i = 0; i < jArray.length(); i++) {
                        if (jArray.getJSONObject(i).getLong("nr_unic") != nr_unic) {
                            if (total != 0) {
                                pv = new Proformevalues();
                                pv.setCod_fiscal(cod_f);
                                pv.setNr_unic(nr_unic_vechi);
                                pv.setClient(furn);
                                pv.setNrFact(nrFact);
                                pv.setData(dataF);
                                pv.setDenProdus("TOTAL:");
                                pv.setPret_livr(total);
                                pv.setParent(false);
                                pv.setVisible(false);
                                list.add(pv);
                            }
                            nr_unic = jArray.getJSONObject(i).getLong("nr_unic");
                            pv = new Proformevalues();
                            pv.setNr_unic(nr_unic);
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
                        pv.setNr_unic(nr_unic);
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
                        nr_unic_vechi = jArray.getJSONObject(i).getLong("nr_unic");
                        if (i == (jArray.length() - 1)) {
                            pv = new Proformevalues();
                            pv.setCod_fiscal(cod_f);
                            pv.setNr_unic(nr_unic_vechi);
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

                  //  if(adapter == null) {
                        adapter = new ProformeSalavateAdapter(getBaseContext(), list, FacturiActivity.this);
                        recyclerView.setAdapter(adapter);
                //    } else {

                //        adapter.notifyDataSetChanged();
               //     }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "Verifica conexiunea de internet. Este necesara pentru obtinere numar proforma...", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
