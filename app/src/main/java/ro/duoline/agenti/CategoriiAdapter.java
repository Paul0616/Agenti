package ro.duoline.agenti;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 10/8/2017.
 */

public class CategoriiAdapter extends RecyclerView.Adapter<CategoriiAdapter.ViewHolder> {

    private List<CategoriiValues> categoriiValues;
    private Context context;

    public CategoriiAdapter(Context context, List<CategoriiValues> categoriiValues){
        this.context = context;
        this.categoriiValues = categoriiValues;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView numecategorie;
        private TextView nrProduseInCategorie;
        private TextView nrCurent;

        public ViewHolder(View view) {
            super(view);
            numecategorie = (TextView) view.findViewById(R.id.categorie_text);
            nrProduseInCategorie = (TextView) view.findViewById(R.id.nr_produse_textView);
            nrCurent = (TextView) view.findViewById(R.id.crt_textView);
        }
    }

    @Override
    public CategoriiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_categorii_layout,parent,false);
        return new CategoriiAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return categoriiValues.size();
    }

    @Override
    public void onBindViewHolder(CategoriiAdapter.ViewHolder holder, int position) {
        holder.nrCurent.setText(Integer.toString(position + 1));
        holder.numecategorie.setText(categoriiValues.get(position).getNumeCategorie());
        holder.nrProduseInCategorie.setText(Integer.toString(categoriiValues.get(position).getNrProduseInCategorie()));
    }
}
