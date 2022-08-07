package com.example.okareappv2.ElderUI;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okareappv2.R;

public class StoreRecycleviewAdapter extends RecyclerView.Adapter<StoreRecycleviewAdapter.ViewHolder> {

    private String[][] stream;
    Resources resources;

    public StoreRecycleviewAdapter() {

    }

    public StoreRecycleviewAdapter(String[][] strings, Resources resources) {
        stream=strings;
        this.resources = resources;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycleview_store, viewGroup, false);


        return new ViewHolder(v);
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if(position==0){

            ViewGroup.MarginLayoutParams p= (ViewGroup.MarginLayoutParams)viewHolder.getConstraintLayout().getLayoutParams();


            int px = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16,resources.getDisplayMetrics()));

            p.topMargin = px;
        }


        viewHolder.getTittle().setText(stream[position][0]);
        viewHolder.getAdress().setText(stream[position][1]);
        viewHolder.getNumber().setText(stream[position][2]);
        viewHolder.getDot().setImageTintList(ColorStateList.valueOf(resources.getColor(stream[position][3].equals("1")?R.color.recycle_dot_open:R.color.recycle_dot_close,null)));
        viewHolder.getLocation().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+viewHolder.getTittle().getText());

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                v.getContext().startActivity(mapIntent);

            }
        });

    }

    public int getItemCount() {
        return stream.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tittle,adress,number;
        ImageView dot;
        ImageButton location;
        ConstraintLayout constraintLayout;

        public ViewHolder(View v) {
            super(v);

            //初始化 cell ui
            tittle = v.findViewById(R.id.store_tittle);
            adress = v.findViewById(R.id.store_address);
            number = v.findViewById(R.id.store_phone_nummber);
            dot = v.findViewById(R.id.recycleview_cell_dot);
            location = v.findViewById(R.id.recycleview_cell_button);
            constraintLayout = v.findViewById(R.id.recycleview_cell);
        }

        public TextView getTittle() {
            return tittle;
        }

        public TextView getAdress() {
            return adress;
        }

        public TextView getNumber() {
            return number;
        }

        public ImageView getDot() {
            return dot;
        }

        public ImageButton getLocation() { return location; }

        public ConstraintLayout getConstraintLayout() { return constraintLayout; }
    }

}
