package ro.duoline.agenti;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ButoaneAdapter adapter;

    private ArrayList<ButoaneMeniuPrincipal>  butoane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        butoane = new ArrayList<>();
        listCreate(this);
        adapter = new ButoaneAdapter(getApplicationContext(), butoane);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    //    drawable = ContextCompat.getDrawable(this, R.drawable.ic_action_name);
    }

    private void listCreate(Context context){

        ButoaneMeniuPrincipal btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorComanda));
        btn.setTextButon("COMANDA");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_name));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorScanare));
        btn.setTextButon("SCANARE / INVENTARIERE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorSalvate));
        btn.setTextButon("PROFORME SALVATE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorProforme));
        btn.setTextButon("PROFORME");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorFacturi));
        btn.setTextButon("FACTURI");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorMemo));
        btn.setTextButon("MEMO");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
        btn = new ButoaneMeniuPrincipal();
        btn.setCuloareButon(context.getColor(R.color.colorDeconectare));
        btn.setTextButon("DECONECTARE");
        btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scan));
        butoane.add(btn);
    }
}
