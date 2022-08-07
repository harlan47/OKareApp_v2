package com.example.okareappv2.ElderUI;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.okareappv2.R;

public class InfoFragment extends Fragment implements View.OnClickListener {


    public static final String TAG = "InfoFragment";

    ConstraintLayout infoConsBtn1,infoConsBtn2,infoConsBtn3,infoConsBtn4;


    public InfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_info, container, false);


        infoConsBtn1 = root.findViewById(R.id.info_image_button1);
        infoConsBtn2 = root.findViewById(R.id.info_image_button2);
        infoConsBtn3 = root.findViewById(R.id.info_image_button3);
        infoConsBtn4 = root.findViewById(R.id.info_image_button4);


        setOnclick();


        return root;
    }

    public void setOnclick(){
        infoConsBtn1.setOnClickListener(this);
        infoConsBtn2.setOnClickListener(this);
        infoConsBtn3.setOnClickListener(this);
        infoConsBtn4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //網址

        switch(v.getId()){
            case R.id.info_image_button1:
                // 網址..
                Toast.makeText(v.getContext(),"第1",Toast.LENGTH_SHORT).show();
                break;
            case R.id.info_image_button2:
                // 網址..
                Toast.makeText(v.getContext(),"第2",Toast.LENGTH_SHORT).show();
                break;
            case R.id.info_image_button3:
                // 網址..
                Toast.makeText(v.getContext(),"第3",Toast.LENGTH_SHORT).show();
                break;
            case R.id.info_image_button4:
                // 網址..
                Toast.makeText(v.getContext(),"第4",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        //連結到網址

    }
}