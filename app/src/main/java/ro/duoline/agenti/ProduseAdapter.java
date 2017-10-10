package ro.duoline.agenti;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Paul on 10/9/2017.
 */

public class ProduseAdapter extends RecyclerView.Adapter<ProduseAdapter.ViewHolder> {
    private List<ProduseValues> prosuseValues;
    private Context context;

    public ProduseAdapter(Context context, List<ProduseValues> prosuseValues){
        this.context = context;
        setValues(prosuseValues);

    }

    public  void setValues(List<ProduseValues> prosuseValues){
        this.prosuseValues = prosuseValues;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView denumireProdusTextView, stocTextView, rezervataTextView, tvaTextView, pretTextView, ramaseTextView;
        private TextView inCosTextView, umTextView1, umTextView2, umTextView3, umTextView4, etichInCos;
        private Button butonPlus, butonMinus;
        private LinearLayout fundalComanda;

        public ViewHolder(View view) {
            super(view);
            denumireProdusTextView = (TextView) view.findViewById(R.id.textViewDenumire);
            stocTextView = (TextView) view.findViewById(R.id.textViewStoc);
            rezervataTextView = (TextView) view.findViewById(R.id.textViewRezervata);
            umTextView1 = (TextView) view.findViewById(R.id.textViewBuc1);
            umTextView2 = (TextView) view.findViewById(R.id.textViewBuc2);
            umTextView3 = (TextView) view.findViewById(R.id.textViewBuc3);
            umTextView4 = (TextView) view.findViewById(R.id.textViewBuc4);
            tvaTextView = (TextView) view.findViewById(R.id.textViewTVA);
            ramaseTextView = (TextView) view.findViewById(R.id.textViewRamase);
            pretTextView = (TextView) view.findViewById(R.id.textViewPret);
            inCosTextView = (TextView) view.findViewById(R.id.textViewInCos);
            etichInCos = (TextView) view.findViewById(R.id.textView8);
            fundalComanda = (LinearLayout) view.findViewById(R.id.layout_fundal_comanda);

            butonPlus = (Button) view.findViewById(R.id.buttonPlus);
            butonMinus = (Button) view.findViewById(R.id.buttonMinus);
         //   nrProduseInCategorie = (TextView) view.findViewById(R.id.nr_produse_textView);
         //   nrCurent = (TextView) view.findViewById(R.id.crt_textView);
            butonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int comandate = prosuseValues.get(getAdapterPosition()).getComandate() + 1;
                    prosuseValues.get(getAdapterPosition()).setComandate(comandate);
                    if(prosuseValues.get(getAdapterPosition()).getComandate() > 0){
                       // inCosTextView.setVisibility(View.VISIBLE);
                       // umTextView4.setVisibility(View.VISIBLE);
                       // etichInCos.setVisibility(View.VISIBLE);
                        fundalComanda.setVisibility(View.VISIBLE);
                        if(prosuseValues.get(getAdapterPosition()).getComandate() >= prosuseValues.get(getAdapterPosition()).getStoc()){
                            fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorComanda));
                        } else {
                            fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorFacturi));
                        }
                        notifyDataSetChanged();
                    }

                }
            });
            butonMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int comandate = prosuseValues.get(getAdapterPosition()).getComandate() - 1;
                    if(comandate >= 0) {
                        prosuseValues.get(getAdapterPosition()).setComandate(comandate);
                    }
                    if(prosuseValues.get(getAdapterPosition()).getComandate() > 0){
                        //inCosTextView.setVisibility(View.VISIBLE);
                       // umTextView4.setVisibility(View.VISIBLE);
                       // etichInCos.setVisibility(View.VISIBLE);
                        fundalComanda.setVisibility(View.VISIBLE);
                    }
                    if(prosuseValues.get(getAdapterPosition()).getComandate() == 0){
                       // inCosTextView.setVisibility(View.INVISIBLE);
                       // umTextView4.setVisibility(View.INVISIBLE);
                       // etichInCos.setVisibility(View.INVISIBLE);
                        fundalComanda.setVisibility(View.INVISIBLE);
                    }
                    if(prosuseValues.get(getAdapterPosition()).getComandate() >= prosuseValues.get(getAdapterPosition()).getStoc()){
                        fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorComanda));
                    } else {
                        fundalComanda.setBackgroundColor(ContextCompat.getColor(context,R.color.colorFacturi));
                    }

                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public ProduseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_produse_layout,parent,false);
        return new ProduseAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return prosuseValues.size();
    }

    @Override
    public void onBindViewHolder(ProduseAdapter.ViewHolder holder, int position) {
        holder.denumireProdusTextView.setText(prosuseValues.get(position).getDenumire());
        holder.stocTextView.setText(Integer.toString(prosuseValues.get(position).getStoc()));
        holder.rezervataTextView.setText(Integer.toString(prosuseValues.get(position).getRezervata()));
        holder.ramaseTextView.setText(Integer.toString(prosuseValues.get(position).getStoc() - prosuseValues.get(position).getRezervata()));
        holder.umTextView1.setText(prosuseValues.get(position).getUm());
        holder.umTextView2.setText(prosuseValues.get(position).getUm());
        holder.umTextView3.setText(prosuseValues.get(position).getUm());
        holder.umTextView4.setText(prosuseValues.get(position).getUm());
        holder.tvaTextView.setText(Integer.toString(prosuseValues.get(position).getTva()));
        holder.pretTextView.setText(Float.toString(prosuseValues.get(position).getPret_livr()));
        holder.inCosTextView.setText(Integer.toString(prosuseValues.get(position).getComandate()));
        if(prosuseValues.get(position).getComandate() > 0) {
           // holder.inCosTextView.setVisibility(View.VISIBLE);
           // holder.umTextView4.setVisibility(View.VISIBLE);
          //  holder.etichInCos.setVisibility(View.VISIBLE);
            holder.fundalComanda.setVisibility(View.VISIBLE);
        } else {
          //  holder.inCosTextView.setVisibility(View.INVISIBLE);
          //  holder.umTextView4.setVisibility(View.INVISIBLE);
          //  holder.etichInCos.setVisibility(View.INVISIBLE);
            holder.fundalComanda.setVisibility(View.INVISIBLE);
        }
    }
}
