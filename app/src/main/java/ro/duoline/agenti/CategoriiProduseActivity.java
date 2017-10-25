package ro.duoline.agenti;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

public class CategoriiProduseActivity extends AppCompatActivity {
    private RecyclerView recyclerCategorii;
    private RecyclerView.LayoutManager layoutManager;
    private CategoriiAdapter adapter;
    private ToggleButton tglBtn;
    private EditText editTextFiltru;
    private TextView toateProduseleTextView, totalProduseTextView;
    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SaveSharedPreference.getStyle(this));
        setContentView(R.layout.activity_categorii_produse);
        this.setTitle("Categorii Produse");
        recyclerCategorii = (RecyclerView) findViewById(R.id.recycler_categorii);
        tglBtn = (ToggleButton) findViewById(R.id.toggleButton2);
        editTextFiltru = (EditText) findViewById(R.id.editTextFiltru);
        toateProduseleTextView = (TextView) findViewById(R.id.produs_text_view);
        totalProduseTextView = (TextView) findViewById(R.id.count_produse_text_view);
        totalProduseTextView.setText(Integer.toString(controller.getNrProduse(this, true)));
        adapter = new CategoriiAdapter(this, controller.getCategoriiFiltrate(true, ""));
        layoutManager = new LinearLayoutManager(this);
        recyclerCategorii.setLayoutManager(layoutManager);
        recyclerCategorii.addItemDecoration(new LineItemDecoration(this, null));
        recyclerCategorii.setAdapter(adapter);

        toateProduseleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProduseActivity.class);
                getApplicationContext().startActivity(i);
            }
        });

        tglBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    adapter.setValues(controller.getCategoriiFiltrate(isChecked, editTextFiltru.getText().toString()));
                    adapter.notifyDataSetChanged();
                totalProduseTextView.setText(Integer.toString(controller.getNrProduse(getBaseContext(), isChecked)));
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
                adapter.setValues(controller.getCategoriiFiltrate(tglBtn.isChecked(), s.toString()));
                adapter.notifyDataSetChanged();
            }
        });
    }
}
