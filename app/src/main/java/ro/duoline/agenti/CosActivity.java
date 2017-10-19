package ro.duoline.agenti;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class CosActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    DBController controller = new DBController(this);
    private RecyclerView recyclerCos;
    private TextView total;
    private RecyclerView.LayoutManager layoutManager;
    private CosAdapter adapter;
    private List<ProduseValues> listaCos; //, listaCos1;
    private LinearLayout cosgol;
    private Button bParteneri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cos);
        this.setTitle("Cos Produse");
        controller.deleteZeroValuesfromCos();
        cosgol = (LinearLayout) findViewById(R.id.linearLayoutCosGol);
        recyclerCos = (RecyclerView) findViewById(R.id.recyclerCos);
        bParteneri = (Button) findViewById(R.id.buttonParteneri);
        listaCos = controller.getCos();
    //    listaCos1 = controller.getCosAll();

        total = (TextView) findViewById(R.id.textViewTotal);
        setTotal();
        adapter = new CosAdapter(this, listaCos, controller, this, 1);
        layoutManager = new LinearLayoutManager(this);
        recyclerCos.setLayoutManager(layoutManager);
        int iColor = Color.parseColor("#999999");
        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);

        recyclerCos.addItemDecoration(new LineItemDecoration(this, colorFilter));
        recyclerCos.setAdapter(adapter);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerCos);

        bParteneri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), Parteneri.class);
                startActivity(i);
            }
        });
    }

    public void setupCosGol(){
        if (listaCos.size() == 0){
            cosgol.setVisibility(View.VISIBLE);
            bParteneri.setVisibility(View.INVISIBLE);
        } else {
            cosgol.setVisibility(View.INVISIBLE);
            bParteneri.setVisibility(View.VISIBLE);
        }
    }

    public void setTotal(){
        total.setText("Total: "+Float.toString(calculTotal())+" lei");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCosGol();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CosAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            //String name = cartList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            //final Item deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            /*
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            */
        }
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
}
