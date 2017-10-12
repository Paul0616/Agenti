package ro.duoline.agenti;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

/**
 * Created by Paul on 10/11/2017.
 */

public class CosAdapter extends RecyclerView.Adapter<CosAdapter.ViewHolder>{

    private List<ProduseValues> produseValues;
    private Context context;
    private DBController controller;
    CosActivity mInstance;

    public CosAdapter(Context context, List<ProduseValues> produseValues, DBController controller, CosActivity mInstance){
        this.context = context;
        this.controller = controller;
        this.mInstance = mInstance;
        setValues(produseValues);
    }

    public void setValues(List<ProduseValues> prosuseValues){
        this.produseValues = prosuseValues;
    }
/*
    public float calculTotal() {
        float res = 0;
        for(int i=0; i<felMeniu.size(); i++){
            res += (felMeniu.get(i).getBucComandate() * felMeniu.get(i).getPret());
        }
        return round2(res, 2);
    }
  */
    public float round2(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    public void removeItem(int pos){


        controller.deleteItemfromCos(produseValues.get(pos).getCodProdus());
        produseValues.remove(pos);
        notifyItemRemoved(pos);
    }


    @Override
    public CosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_cos_layout,parent,false);
        return new CosAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return produseValues.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.denumireProdusTextView.setText(produseValues.get(position).getDenumire());
        holder.umTextView.setText(produseValues.get(position).getUm());
        holder.tvaTextView.setText(Integer.toString(produseValues.get(position).getTva()));
        holder.pretTextView.setText(Float.toString(produseValues.get(position).getPret_livr()));
        holder.inCosTextView.setText(Integer.toString(produseValues.get(position).getComandate()));
        Float f = produseValues.get(position).getPret_livr() * produseValues.get(position).getComandate();
        holder.subtotalTextView.setText(Float.toString(round2(f,2)));
        if(produseValues.get(position).getComandate() > 0) {
            holder.fundalComanda.setVisibility(View.VISIBLE);
        } else {
            holder.fundalComanda.setVisibility(View.INVISIBLE);
        }
        if(produseValues.get(position).getComandate() >= (produseValues.get(position).getStoc() - produseValues.get(position).getRezervata())){
            holder.fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorComanda));
        } else {
            holder.fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorFacturi));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView denumireProdusTextView, tvaTextView, pretTextView, subtotalTextView;
        private TextView inCosTextView, umTextView;
        private Button butonPlus, butonMinus;
        public LinearLayout fundalComanda, viewForeground;
        public RelativeLayout viewBackground;



        public ViewHolder(View view) {
            super(view);
            denumireProdusTextView = (TextView) view.findViewById(R.id.textViewDenumireCos);
            umTextView = (TextView) view.findViewById(R.id.textViewBucCos);
            tvaTextView = (TextView) view.findViewById(R.id.textViewTVACos);
            pretTextView = (TextView) view.findViewById(R.id.textViewPretCos);
            inCosTextView = (TextView) view.findViewById(R.id.textViewInCos1);
            fundalComanda = (LinearLayout) view.findViewById(R.id.layout_fundal_comandaCos);
            subtotalTextView = (TextView) view.findViewById(R.id.textViewSubtotalCos);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);
            viewForeground = (LinearLayout) view.findViewById(R.id.view_foreground);
            butonPlus = (Button) view.findViewById(R.id.buttonPlusCos);
            butonMinus = (Button) view.findViewById(R.id.buttonMinusCos);

            butonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int comandate = produseValues.get(getAdapterPosition()).getComandate() + 1;
                    produseValues.get(getAdapterPosition()).setComandate(comandate);
                    controller.setProdusComandat(produseValues.get(getAdapterPosition()).getCodProdus(), comandate);
                    if(produseValues.get(getAdapterPosition()).getComandate() > 0){
                        fundalComanda.setVisibility(View.VISIBLE);
                        if(produseValues.get(getAdapterPosition()).getComandate() >= (produseValues.get(getAdapterPosition()).getStoc() - produseValues.get(getAdapterPosition()).getRezervata())){
                            fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorComanda));
                        } else {
                            fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorFacturi));
                        }
                        mInstance.setTotal();
                        notifyDataSetChanged();
                    }

                }
            });
            butonMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int comandate = produseValues.get(getAdapterPosition()).getComandate() - 1;
                    if(comandate >= 0) {
                        produseValues.get(getAdapterPosition()).setComandate(comandate);
                        controller.setProdusComandat(produseValues.get(getAdapterPosition()).getCodProdus(), comandate);
                    }
                    if(produseValues.get(getAdapterPosition()).getComandate() > 0){
                        fundalComanda.setVisibility(View.VISIBLE);
                    }
                    if(produseValues.get(getAdapterPosition()).getComandate() == 0){
                        fundalComanda.setVisibility(View.INVISIBLE);
                    }
                    if(produseValues.get(getAdapterPosition()).getComandate() >= (produseValues.get(getAdapterPosition()).getStoc() - produseValues.get(getAdapterPosition()).getRezervata())){
                        fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorComanda));
                    } else {
                        fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorFacturi));
                    }
                    mInstance.setTotal();
                    notifyDataSetChanged();
                }
            });

        }
    }
}
