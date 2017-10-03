package ro.duoline.agenti;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ButoaneAdapter adapter;
    private Context context;
    private String numeDbFirma, client, passw;
    final String FIRMA = "test";
    final String CLIENT = "agent1";
    final String PAROLA = "11";
    private AlertDialog alertDialog;

    private ArrayList<ButoaneMeniuPrincipal>  butoane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        butoane = new ArrayList<>();
        listCreate(context);
        adapter = new ButoaneAdapter(getApplicationContext(), butoane);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (numeDbFirma == null && client == null && passw == null){
            cereParola();
        }
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    //    drawable = ContextCompat.getDrawable(this, R.drawable.ic_action_name);
    }

    //*****************functie pentru afisare introducerii parolei**************
    public void cereParola(){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.cere_parola, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText numeFirma = (EditText) promptsView.findViewById(R.id.editTextNume);
        final EditText user = (EditText) promptsView.findViewById(R.id.editTextUser);
        final EditText parola = (EditText) promptsView.findViewById(R.id.editTextParola);


        //set dialog message
        alertDialogBuilder.setCancelable(false)
               .setPositiveButton("OK", null);

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                numeDbFirma = numeFirma.getText().toString();
                client = user.getText().toString();
                passw = parola.getText().toString();
                if(FIRMA.equals(numeDbFirma) && CLIENT.equals(client) && PAROLA.equals(passw)){
                    alertDialog.dismiss();
                } else {
                   if(!FIRMA.equals(numeDbFirma)){
                       numeFirma.setError("Firma nu exista");
                   }
                    if(!CLIENT.equals(client)){
                        user.setError("User name gresit");
                    }
                    if(!PAROLA.equals(passw)){
                        parola.setError("Parola gresita");
                    }
                }
            }
        });


    }
    //*****************************************************************

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
