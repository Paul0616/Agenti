package ro.duoline.agenti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ToggleButton;

public class ProduseActivity extends AppCompatActivity {

    private RecyclerView recyclerProduse;
    private RecyclerView.LayoutManager layoutManager;
    private ProduseAdapter adapter;
    private ToggleButton tglBtn;
    private EditText editTextFiltru;
    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produse);
        this.setTitle("Toate Produsele");
        recyclerProduse = (RecyclerView) findViewById(R.id.recycler_produse);
        tglBtn = (ToggleButton) findViewById(R.id.toggleButton3);
        editTextFiltru = (EditText) findViewById(R.id.editTextFiltru1);
        adapter = new ProduseAdapter(this, controller.getProduse());
        layoutManager = new LinearLayoutManager(this);
        recyclerProduse.setLayoutManager(layoutManager);
        recyclerProduse.setAdapter(adapter);
    }
}
