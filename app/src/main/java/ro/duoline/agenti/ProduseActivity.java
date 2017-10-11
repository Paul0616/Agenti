package ro.duoline.agenti;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

public class ProduseActivity extends AppCompatActivity {

    private RecyclerView recyclerProduse;
    private RecyclerView.LayoutManager layoutManager;
    private ProduseAdapter adapter;
    private ToggleButton tglBtn;
    private EditText editTextFiltru;
    private String clasa = "";
    DBController controller = new DBController(this);
    private Button btnCos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produse);
        clasa = getIntent().getStringExtra("clasa");
        if(clasa == null) clasa = "";
        if(clasa.isEmpty()) {
            this.setTitle("Toate Produsele");
        } else {
            this.setTitle(clasa);
        }
        recyclerProduse = (RecyclerView) findViewById(R.id.recycler_produse);
        tglBtn = (ToggleButton) findViewById(R.id.toggleButton3);
        btnCos = (Button) findViewById(R.id.buttonCos);
        editTextFiltru = (EditText) findViewById(R.id.editTextFiltru1);
        adapter = new ProduseAdapter(this, controller.getProduse(true, "", clasa), controller);
        layoutManager = new LinearLayoutManager(this);
        recyclerProduse.setLayoutManager(layoutManager);
        int iColor = Color.parseColor("#cdcdcd");
        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);

        recyclerProduse.addItemDecoration(new LineItemDecoration(this, colorFilter));
        recyclerProduse.setAdapter(adapter);
        btnCos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CosActivity.class);
                startActivity(i);
            }
        });
        tglBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setValues(controller.getProduse(isChecked, editTextFiltru.getText().toString(), clasa));
                adapter.notifyDataSetChanged();
            }
        });

        editTextFiltru.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.setValues(controller.getProduse(tglBtn.isChecked(), s.toString(), clasa));
                adapter.notifyDataSetChanged();
            }
        });


    }


}
