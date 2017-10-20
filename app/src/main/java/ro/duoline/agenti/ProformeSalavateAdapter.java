package ro.duoline.agenti;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Paul on 20.10.2017.
 */

public class ProformeSalavateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PARENT = 0;
    private static final int TYPE_CHILD = 1;
    private List<Proformevalues> proformeSalvate;
    private Context context;

    public ProformeSalvateLocal(Context context, List<Proformevalues> proforme){
        this.context = context;
        this.proformeSalvate = proforme;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_PARENT){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_proforma_salvata_parent,parent,false);
            return new ParentViewHolder(v);
        }
        if(viewType == TYPE_CHILD){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_proformaview_layout,parent,false);
            return new ChildViewHolder(v);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if(holder instanceof ParentViewHolder){
            if(proformeSalvate.get(position).getClient() != null){
                ((ParentViewHolder) holder).client.setText(proformeSalvate.get(position).getClient());
            } else {
                ((ParentViewHolder) holder).client.setText("");
            }
            if(proformeSalvate.get(position).getClient() != null){
                ((ParentViewHolder) holder).dataProforma.setText(proformeSalvate.get(position).getData());
            } else {
                ((ParentViewHolder) holder).client.setText("");
            }
        } else if (holder instanceof ChildViewHolder){

        }
    }

    @Override
    public int getItemCount() {
        return proformeSalvate.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(proformeSalvate.get(position).getParent()) return TYPE_PARENT;
        return TYPE_CHILD;
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder{
        public TextView client, dataProforma;
        public ParentViewHolder(View itemView){
            super(itemView);
            client = (TextView) itemView.findViewById(R.id.clientTextView);
            dataProforma = (TextView) itemView.findViewById(R.id.textData);

        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder{
        private TextView denumireProdusTextView, tvaTextView, pretTextView, subtotalTextView, nrCrt, cantit, um;
        public ChildViewHolder(View itemView){
            super(itemView);
            denumireProdusTextView = (TextView) itemView.findViewById(R.id.textViewDenumireProf);
            nrCrt = (TextView) itemView.findViewById(R.id.textViewnrCrtProf);
            cantit = (TextView) itemView.findViewById(R.id.textViewCantProf);
            um = (TextView) itemView.findViewById(R.id.textViewUmProf);
            pretTextView = (TextView) itemView.findViewById(R.id.textViewPretProf);
            tvaTextView = (TextView) itemView.findViewById(R.id.textViewTVAProf);
            subtotalTextView = (TextView) itemView.findViewById(R.id.textViewSubtotalProf);
        }
    }
}
