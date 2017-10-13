package ro.duoline.agenti;


import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;


import android.database.Cursor;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ProformaView extends AppCompatActivity {//implements LoaderCallbacks<Cursor>
    private TextView nrProforma, dataProforma, totalProforma, clientProforma;
    private RecyclerView recyclerViewProforma;
    private Button mEmiteButon;
    private LinearLayoutManager layoutManager;
    DBController controller = new DBController(this);

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
        clientProforma.setText(controller.getClientFromCos());

    }

}

