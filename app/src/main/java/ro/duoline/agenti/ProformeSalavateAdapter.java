package ro.duoline.agenti;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 20.10.2017.
 */

public class ProformeSalavateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PARENT = 0;
    private static final int TYPE_CHILD = 1;
    private List<Proformevalues> proformeSalvate, proformeSalvateFiltrate;
    private Context context;
    AppCompatActivity mInstance;


    public ProformeSalavateAdapter(Context context, List<Proformevalues> proforme, AppCompatActivity mInstance){
        this.context = context;
        this.proformeSalvate = proforme;
        if(mInstance instanceof ProformeSalvateLocalActivity) {
            this.mInstance = (ProformeSalvateLocalActivity) mInstance;
        } else if (mInstance instanceof ViewServerProformeActivity){
            this.mInstance = (ViewServerProformeActivity) mInstance;
        }
        this.proformeSalvateFiltrate = new ArrayList<>();
        for (int i = 0; i<proformeSalvate.size(); i++){
            if(proformeSalvate.get(i).getVisible()) proformeSalvateFiltrate.add(proformeSalvate.get(i));
        }
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ParentViewHolder){
            if(proformeSalvateFiltrate.get(position).getNrFact() != null){
                if(proformeSalvateFiltrate.get(position).getNrFact() != 0) {
                    ((ParentViewHolder) holder).nrFact.setText(proformeSalvateFiltrate.get(position).getNrFact().toString());
                } else {
                    ((ParentViewHolder) holder).nrFact.setText("BON");
                }
            } else {
                ((ParentViewHolder) holder).nrFact.setText("");
            }

            if(proformeSalvateFiltrate.get(position).getClient() != null){
                ((ParentViewHolder) holder).client.setText(proformeSalvateFiltrate.get(position).getClient());
            } else {
                ((ParentViewHolder) holder).client.setText("");
            }
            if(proformeSalvateFiltrate.get(position).getData() != null){
                ((ParentViewHolder) holder).dataProforma.setText(proformeSalvateFiltrate.get(position).getData());
            } else {
                ((ParentViewHolder) holder).client.setText("");
            }
        } else if (holder instanceof ChildViewHolder){
            if(proformeSalvateFiltrate.get(position).getDenProdus() != null){
                ((ChildViewHolder) holder).denumireProdusTextView.setText(proformeSalvateFiltrate.get(position).getDenProdus());
            } else {
                ((ChildViewHolder) holder).denumireProdusTextView.setText("");
            }
            if(proformeSalvateFiltrate.get(position).getNrCrt() != null){
                ((ChildViewHolder) holder).nrCrt.setText(proformeSalvateFiltrate.get(position).getNrCrt().toString() + ".");
            } else {
                ((ChildViewHolder) holder).nrCrt.setText("");
            }
            if(proformeSalvateFiltrate.get(position).getBuc() != null){
                ((ChildViewHolder) holder).cantit.setText(proformeSalvateFiltrate.get(position).getBuc().toString());
            } else {
                ((ChildViewHolder) holder).cantit.setText("");
            }
            if(proformeSalvateFiltrate.get(position).getUm() != null){
                ((ChildViewHolder) holder).um.setText(proformeSalvateFiltrate.get(position).getUm().toString());
            } else {
                ((ChildViewHolder) holder).um.setText("");
            }
            if(proformeSalvateFiltrate.get(position).getPret_livr() != null){
                ((ChildViewHolder) holder).pretTextView.setText(proformeSalvateFiltrate.get(position).getPret_livr().toString());
            } else {
                ((ChildViewHolder) holder).pretTextView.setText("");
            }
            if(proformeSalvateFiltrate.get(position).getTva() != null){
                ((ChildViewHolder) holder).tvaTextView.setText(proformeSalvateFiltrate.get(position).getTva().toString());
            } else {
                ((ChildViewHolder) holder).tvaTextView.setText("");
            }
            if (proformeSalvateFiltrate.get(position).getBuc() != null) {
                Float subtotal = round2((proformeSalvateFiltrate.get(position).getPret_livr() * proformeSalvateFiltrate.get(position).getBuc()), 2);
                ((ChildViewHolder) holder).subtotalTextView.setText(subtotal.toString());
                ((ChildViewHolder) holder).textview13.setVisibility(View.VISIBLE);
                ((ChildViewHolder) holder).textview14.setVisibility(View.VISIBLE);
                ((ChildViewHolder) holder).layoutButoane.setVisibility(View.INVISIBLE);
                ((ChildViewHolder) holder).linie.setVisibility(View.INVISIBLE);
            } else {
                ((ChildViewHolder) holder).subtotalTextView.setText(proformeSalvateFiltrate.get(position).getPret_livr().toString());
                ((ChildViewHolder) holder).pretTextView.setText(proformeSalvateFiltrate.get(position).getDenProdus());
                ((ChildViewHolder) holder).textview13.setVisibility(View.INVISIBLE);
                ((ChildViewHolder) holder).textview14.setVisibility(View.INVISIBLE);
                ((ChildViewHolder) holder).denumireProdusTextView.setText("");
                if(mInstance instanceof ProformeSalvateLocalActivity) {
                    ((ChildViewHolder) holder).layoutButoane.setVisibility(View.VISIBLE);
                    ((ChildViewHolder) holder).linie.setVisibility(View.INVISIBLE);
                } else {
                    ((ChildViewHolder) holder).layoutButoane.setVisibility(View.INVISIBLE);
                    ((ChildViewHolder) holder).linie.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return proformeSalvateFiltrate.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(proformeSalvateFiltrate.get(position).getParent()) return TYPE_PARENT;
        return TYPE_CHILD;
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder{
        public TextView client, dataProforma, nrFact;
        public ParentViewHolder(View itemView){
            super(itemView);
            client = (TextView) itemView.findViewById(R.id.clientTextView);
            dataProforma = (TextView) itemView.findViewById(R.id.textData);
            nrFact = (TextView) itemView.findViewById(R.id.textviewNrfact);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String client = proformeSalvateFiltrate.get(getAdapterPosition()).getClient();
                    String data = proformeSalvateFiltrate.get(getAdapterPosition()).getData();
                    int nrProforma = 0;
                    if(proformeSalvateFiltrate.get(getAdapterPosition()).getNrFact() != null) {
                        nrProforma = proformeSalvateFiltrate.get(getAdapterPosition()).getNrFact();
                    }
                    proformeSalvateFiltrate.clear();
                    notifyItemRangeRemoved(0, proformeSalvateFiltrate.size()-1);

                    allChildrensInvisible();
                    for(int i=0; i<proformeSalvate.size(); i++){
                        if(proformeSalvate.get(i).getNrFact() == null ){
                            if(proformeSalvate.get(i).getClient().equals(client) && proformeSalvate.get(i).getData().equals(data)) proformeSalvate.get(i).setVisible(true);
                        } else {
                            if(proformeSalvate.get(i).getNrFact() == nrProforma) proformeSalvate.get(i).setVisible(true);
                        }

                        if(proformeSalvate.get(i).getVisible()) proformeSalvateFiltrate.add(proformeSalvate.get(i));
                      //notifyItemChanged(i);
                        //
                    }
                    if(mInstance instanceof ProformeSalvateLocalActivity){
                        notifyItemRangeChanged(0, proformeSalvateFiltrate.size());
                    }else{
                        notifyDataSetChanged();
                    }
                    /*
                    if(mInstance instanceof FacturiActivity){
                        ((FacturiActivity) mInstance).layoutManager.scrollToPosition(getAdapterPosition());
                    }
                    */
                }
            });
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder{
        private TextView denumireProdusTextView, tvaTextView, pretTextView, subtotalTextView, nrCrt, cantit, um, textview14, textview13, butTrimite, butSterge;
        private LinearLayout layoutButoane, linie;
        public ChildViewHolder(View itemView){
            super(itemView);
            denumireProdusTextView = (TextView) itemView.findViewById(R.id.textViewDenumireProf);
            nrCrt = (TextView) itemView.findViewById(R.id.textViewnrCrtProf);
            cantit = (TextView) itemView.findViewById(R.id.textViewCantProf);
            um = (TextView) itemView.findViewById(R.id.textViewUmProf);
            pretTextView = (TextView) itemView.findViewById(R.id.textViewPretProf);
            tvaTextView = (TextView) itemView.findViewById(R.id.textViewTVAProf);
            textview14 = (TextView) itemView.findViewById(R.id.textView14);
            textview13 = (TextView) itemView.findViewById(R.id.textView13);
            subtotalTextView = (TextView) itemView.findViewById(R.id.textViewSubtotalProf);
            layoutButoane = (LinearLayout) itemView.findViewById(R.id.butoanePtProformeSalvate);
            linie = (LinearLayout) itemView.findViewById(R.id.linie);
            butTrimite = (TextView) itemView.findViewById(R.id.textViewButTrimite);
            butSterge = (TextView) itemView.findViewById(R.id.textViewbutSterge);

            butTrimite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ProformaViewActivity.class);
                    i.putExtra("cod_fiscal", proformeSalvateFiltrate.get(getAdapterPosition()).getCod_fiscal());
                    i.putExtra("data", proformeSalvateFiltrate.get(getAdapterPosition()).getData());
                    ((ProformeSalvateLocalActivity) mInstance).startActivity(i);
                }
            });
            butSterge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cod_fiscal = proformeSalvateFiltrate.get(getAdapterPosition()).getCod_fiscal();
                    String data = proformeSalvateFiltrate.get(getAdapterPosition()).getData();
                    stergeProformaSalvate(cod_fiscal, data);
                }
            });
        }
    }

    public void allChildrensInvisible(){
        for(int i=0; i<proformeSalvate.size(); i++){
            if(!proformeSalvate.get(i).getParent()) {
                proformeSalvate.get(i).setVisible(false);
               //notifyItemRemoved(i);
            }

        }
    }

    public void stergeProformaSalvate(String cod_fiscal, String data){

        for(int i=proformeSalvateFiltrate.size()-1; i>=0; i--){
            if(proformeSalvateFiltrate.get(i).getCod_fiscal().equals(cod_fiscal) && proformeSalvateFiltrate.get(i).getData().equals(data)) {
                proformeSalvateFiltrate.remove(i);
                notifyItemRemoved(i);
            }

        }
        for (int i = proformeSalvate.size()-1; i>=0; i--){
            if(proformeSalvate.get(i).getCod_fiscal().equals(cod_fiscal) && proformeSalvate.get(i).getData().equals(data)) {
                proformeSalvate.remove(i);
            }

        }
        ((ProformeSalvateLocalActivity) mInstance).controller.deleteItemfromCosWithCodFiscal(cod_fiscal, data);
    }

    public float round2(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }
}
