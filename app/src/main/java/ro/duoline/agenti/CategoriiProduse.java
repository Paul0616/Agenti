package ro.duoline.agenti;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class CategoriiProduse extends AppCompatActivity {
    private RecyclerView recyclerCategorii;
    private RecyclerView.LayoutManager layoutManager;
    private CategoriiAdapter adapter;
    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorii_produse);
        this.setTitle("Categorii Produse");
        recyclerCategorii = (RecyclerView) findViewById(R.id.recycler_categorii);
        //controller.getProduse(this);
        adapter = new CategoriiAdapter(this, controller.getCategorii());
        layoutManager = new LinearLayoutManager(this);
        recyclerCategorii.setLayoutManager(layoutManager);
        recyclerCategorii.setAdapter(adapter);
    }
}
