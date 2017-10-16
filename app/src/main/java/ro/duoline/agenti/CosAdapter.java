package ro.duoline.agenti;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

public class CosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ProduseValues> produseValues;
    private Context context;
    private DBController controller;
    AppCompatActivity mInstance;
    private int type; //1 inseamna adapter pentru CosActivity si 2 inseamna adapter pentru ProformaViewActivity

    public CosAdapter(Context context, List<ProduseValues> produseValues, DBController controller, AppCompatActivity mInstance, int viewType){
        this.context = context;
        this.controller = controller;
        this.mInstance = mInstance;
        this.type = viewType;
        setValues(produseValues);
    }

    public void setValues(List<ProduseValues> prosuseValues){
        this.produseValues = prosuseValues;
    }

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
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_cos_layout, parent, false);
            return new CosAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_proformaview_layout, parent, false);
            return new CosAdapter.ViewHolder1(view);
        }
    }

    @Override
    public int getItemCount() {
        return produseValues.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CosAdapter.ViewHolder) {
            ((ViewHolder) holder).denumireProdusTextView.setText(produseValues.get(position).getDenumire());
            ((ViewHolder) holder).umTextView.setText(produseValues.get(position).getUm());
            ((ViewHolder) holder).tvaTextView.setText(Integer.toString(produseValues.get(position).getTva()));
            ((ViewHolder) holder).pretTextView.setText(Float.toString(produseValues.get(position).getPret_livr()));
            ((ViewHolder) holder).inCosTextView.setText(Integer.toString(produseValues.get(position).getComandate()));
            Float f = produseValues.get(position).getPret_livr() * produseValues.get(position).getComandate();
            ((ViewHolder) holder).subtotalTextView.setText(Float.toString(round2(f, 2)));
            if (produseValues.get(position).getComandate() > 0) {
                ((ViewHolder) holder).fundalComanda.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder) holder).fundalComanda.setVisibility(View.INVISIBLE);
            }
            if (produseValues.get(position).getComandate() >= (produseValues.get(position).getStoc() - produseValues.get(position).getRezervata())) {
                ((ViewHolder) holder).fundalComanda.setBackgroundColor(ContextCompat.getColor(context, R.color.colorComanda));
            } else {
                ((ViewHolder) holder).fundalComanda.setBackgroundColor(ContextCompat.getColor(context, R.color.colorFacturi));
            }
        } else if(holder instanceof CosAdapter.ViewHolder1){
            ((ViewHolder1) holder).denumireProdusTextView.setText(produseValues.get(position).getDenumire());
            ((ViewHolder1) holder).nrCrtTextView.setText(Integer.toString(position + 1) + ".");
            ((ViewHolder1) holder).inCosTextView.setText(Integer.toString(produseValues.get(position).getComandate()));
            ((ViewHolder1) holder).umTextView.setText(produseValues.get(position).getUm());
            ((ViewHolder1) holder).pretTextView.setText(Float.toString(produseValues.get(position).getPret_livr()));
            ((ViewHolder1) holder).tvaTextView.setText(Integer.toString(produseValues.get(position).getTva()));
            Float f = produseValues.get(position).getPret_livr() * produseValues.get(position).getComandate();
            ((ViewHolder1) holder).subtotalTextView.setText(Float.toString(round2(f, 2)));

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
                        ((CosActivity) mInstance).setTotal();
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
                    ((CosActivity) mInstance).setTotal();
                    notifyDataSetChanged();
                }
            });

        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView denumireProdusTextView, tvaTextView, pretTextView, subtotalTextView;
        private TextView inCosTextView, umTextView, nrCrtTextView;


        public ViewHolder1(View view) {
            super(view);
            denumireProdusTextView = (TextView) view.findViewById(R.id.textViewDenumireProf);
            umTextView = (TextView) view.findViewById(R.id.textViewUmProf);
            tvaTextView = (TextView) view.findViewById(R.id.textViewTVAProf);
            pretTextView = (TextView) view.findViewById(R.id.textViewPretProf);
            inCosTextView = (TextView) view.findViewById(R.id.textViewCantProf);
            subtotalTextView = (TextView) view.findViewById(R.id.textViewSubtotalProf);
           nrCrtTextView = (TextView) view.findViewById(R.id.textViewnrCrtProf);

        }
    }
}
