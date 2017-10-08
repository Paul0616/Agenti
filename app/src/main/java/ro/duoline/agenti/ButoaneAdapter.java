package ro.duoline.agenti;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Paul on 03.10.2017.
 */

public class ButoaneAdapter extends RecyclerView.Adapter<ButoaneAdapter.ViewHolder>{
    private Context context;
    private MainActivity mInstance;
    private ArrayList<ButoaneMeniuPrincipal> butoane;

    public ButoaneAdapter(Context context, ArrayList<ButoaneMeniuPrincipal> btns, MainActivity mInstance){
        this.context = context;
        this.butoane = btns;
        this.mInstance = mInstance;

    }

    @Override
    public ButoaneAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_button_layout,parent,false);
        return new ButoaneAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return butoane.size();
    }

    @Override
    public void onBindViewHolder(ButoaneAdapter.ViewHolder holder, int position) {
       holder.textButon.setText(butoane.get(position).getTextButon());
       holder.iconButon.setImageDrawable(butoane.get(position).getIcon());
       holder.cerc.setBackgroundTintList(ColorStateList.valueOf(butoane.get(position).getCuloareButon()));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textButon;
        private ImageView iconButon;
        private View cerc;

        public ViewHolder(View view) {
            super(view);
            textButon = (TextView) view.findViewById(R.id.buton_text);
            iconButon = (ImageView) view.findViewById(R.id.buton_icon);
            cerc = (View) view.findViewById(R.id.buton_cerc);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.lb);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(butoane.get(getAdapterPosition()).getTextButon().equals("DECONECTARE")){
                        SaveSharedPreference.setLogOut(context);
                        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                    }
                    if(butoane.get(getAdapterPosition()).getTextButon().equals("COMANDA")){
                        Intent i = new Intent(context, CategoriiProduse.class);
                        context.startActivity(i);
                        /*
                        String firma = PreferenceManager.getDefaultSharedPreferences(context).getString("FIRMA", "Agenti");
                        String dateconectare = mInstance.controller.getDateConectare(firma);
                        String contGestiune = PreferenceManager.getDefaultSharedPreferences(context).getString("DEBIT", "");

                        mInstance.loadProduse(dateconectare, contGestiune);
                        */
                    }
                }
            });

        }
    }
}
