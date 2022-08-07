package com.example.okareappv2.familyUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okareappv2.ElderUI.StoreRecycleviewAdapter;
import com.example.okareappv2.R;

import java.util.List;

public class EldersRecycleviewAdapter extends RecyclerView.Adapter {

    String[] elders ;

    public EldersRecycleviewAdapter() {
        elders = new String[9];
        String s = "年長者";
        for(int i =0;i< elders.length;i++){
            elders[i]=s+i;
        }
    }

    public EldersRecycleviewAdapter(String[] elders) {
        this.elders=elders;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_elders, parent, false);
        return new ElderRecycleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==elders.length-1)
            ((ElderRecycleViewHolder)holder).getSegment().setVisibility(View.INVISIBLE);
        ((ElderRecycleViewHolder)holder).getTextView().setText(elders[position]);
    }

    @Override
    public int getItemCount() {
        return elders.length;
    }

    private class ElderRecycleViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        View segment;

        public ElderRecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.recycle_view_elders_text);
            segment= itemView.findViewById(R.id.segment_line3);
        }

        public TextView getTextView() {
            return textView;
        }

        public View getSegment() {
            return segment;
        }
    }

}
