package ro.duoline.agenti;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Paul on 10/12/2017.
 */

public class ParteneriAdapter extends RecyclerView.Adapter<ParteneriAdapter.ViewHolder> {
    private List<ParteneriValues> parteneriValues;
    private Context context;
    private DBController controller;
    Parteneri mInstance;


    public ParteneriAdapter(Context context, List<ParteneriValues> prosuseValues, DBController controller, Parteneri mInstance){
        this.context = context;
        this.controller = controller;
        this.mInstance = mInstance;
        setValues(prosuseValues);

    }

    public  void setValues(List<ParteneriValues> prosuseValues){
        this.parteneriValues = prosuseValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_parteneri_layout,parent,false);
        return new ParteneriAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return parteneriValues.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.denumirePartenerTextView.setText(parteneriValues.get(position).getDenumire());
        holder.denumirePartenerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iterator<ParteneriValues> iterator = parteneriValues.iterator();
                while(iterator.hasNext()){
                    iterator.next().setSelected(false);
                }
                parteneriValues.get(position).setSelected(true);
                mInstance.setButtonEnabled(true, parteneriValues.get(position).getCod_fiscal());
                notifyDataSetChanged();
            }
        });
        if( parteneriValues.get(position).getSelected()){
            holder.denumirePartenerTextView.setBackgroundColor(Color.parseColor("#a1a1a1"));
        } else {
            holder.denumirePartenerTextView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView denumirePartenerTextView;

        public ViewHolder(View view) {
            super(view);
            denumirePartenerTextView = (TextView) view.findViewById(R.id.textViewPartener);

        }
    }
}
