package net.termat.gtfsviewer.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.termat.gtfsviewer.R;

import java.util.List;

/**
 * 時刻表表示のリストアダプタ
 */
public class RecycleDiagramListAdapter extends RecyclerView.Adapter<RecycleDiagramListAdapter.MyViewHolder> {
    private LayoutInflater inf;
    private List<Diagram> diagram;
    private int res;
    private OnClickListsner onlistener;

    public RecycleDiagramListAdapter(@NonNull Context context, int resource, List<Diagram> diagram) {
        inf=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.res=resource;
        this.diagram=diagram;
     }

    @NonNull
    @Override
    public RecycleDiagramListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = inf.inflate(res, parent,false);
        RecycleDiagramListAdapter.MyViewHolder vh = new RecycleDiagramListAdapter.MyViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Diagram dia=diagram.get(position);
        String val=dia.time.substring(0,dia.time.length()-3);
        if(val.length()==4)val="0"+val;
        holder.time.setText(val);
        holder.dist.setText(dia.head);
        holder.layout.setOnClickListener(view -> this.onlistener.onClick(position));
    }

    @Override
    public int getItemCount() {
        if(diagram==null){
            return 0;
        }else{
            return diagram.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public TextView time;
        public TextView dist;

        public MyViewHolder(View view) {
            super(view);
            layout=(LinearLayout)view.findViewById(R.id.diagram_layout);
            time = (TextView)view.findViewById(R.id.diagram_text_item);
            dist = (TextView)view.findViewById(R.id.diagram_text_item2);
        }
    }

    public interface OnClickListsner{
        public void onClick(int index);
    }

    public void setOnItemClickListener(OnClickListsner listener) {
        this.onlistener = listener;
    }
}
