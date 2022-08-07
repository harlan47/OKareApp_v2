package com.example.okareappv2.ElderUI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.okareappv2.R;

public class StoreFragment extends Fragment {


    public static final String TAG = "StoreFragment";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    StoreRecycleviewAdapter adapter ;

    String[][] stream;


    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化資料
        //假設 得到台北通api資料
        stream = new String[10][4];
        for(int i = 0;i<stream.length;i++){
            stream[i][0]="藥局";
            stream[i][1]="地址";
            stream[i][2]="電話-";
            stream[i][3]="1";
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_store, container, false);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.store_recycle_view);


        layoutManager = new LinearLayoutManager(getActivity());


        recyclerView.setLayoutManager(layoutManager);


        adapter = new StoreRecycleviewAdapter(stream,getResources());
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}