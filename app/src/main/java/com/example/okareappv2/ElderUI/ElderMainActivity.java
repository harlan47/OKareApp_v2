package com.example.okareappv2.ElderUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.okareappv2.LogoutButton;
import com.example.okareappv2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ElderMainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    static final String TITTLE_WEATHER = "天氣預報" ;
    static final String TITTLE_INFO = "衛福資訊" ;
    static final String TITTLE_STORE = "藥局門市" ;
    static final String TITTLE_SETTING = "設定" ;

    LinearLayoutCompat navItem1 ,navItem2 ,navItem3,navItem4 ;
    ImageView navItem1Img,navItem2Img,navItem3Img,navItem4Img;
    TextView navItem1Txt,navItem2Txt,navItem3Txt,mainToolbarTittle,navItem4Txt;
    WeatherFragment weatherFragment;
    InfoFragment infoFragment;
    StoreFragment storeFragment;
    FloatingActionButton floatingActionButton;
    SettingFragment settingFragment;
    LogoutButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_main);

        initComponent();

        setOnclickListener();

        onClick(navItem1);



    }



    private void initComponent(){

        navItem1 = findViewById(R.id.nav_item1);
        navItem2 = findViewById(R.id.nav_item2);
        navItem3 = findViewById(R.id.nav_item3);
        navItem4 = findViewById(R.id.nav_item4);

        navItem1Img = findViewById(R.id.nav_item1_img);
        navItem2Img = findViewById(R.id.nav_item2_img);
        navItem3Img = findViewById(R.id.nav_item3_img);
        navItem4Img = findViewById(R.id.nav_item4_img);

        navItem1Txt = findViewById(R.id.nav_item1_txt);
        navItem2Txt = findViewById(R.id.nav_item2_txt);
        navItem3Txt = findViewById(R.id.nav_item3_txt);
        navItem4Txt = findViewById(R.id.nav_item4_txt);

        weatherFragment = new WeatherFragment();
        infoFragment = new InfoFragment();
        storeFragment = new StoreFragment();
        settingFragment = new SettingFragment();

        mainToolbarTittle = findViewById(R.id.main_toolbar_tittle);

        floatingActionButton = findViewById(R.id.SOS_floating_button);

        logoutButton = new LogoutButton(findViewById(R.id.elder_logout),this);

    }

    private void setOnclickListener(){

        navItem1.setOnClickListener(this);
        navItem2.setOnClickListener(this);
        navItem3.setOnClickListener(this);
        navItem4.setOnClickListener(this);

        floatingActionButton.setOnLongClickListener(this);

    }
    private int pre = -1;
    @Override
    public void onClick(View v) {

        int key = v.getId();
        if(key==pre)
            return;

        if(pre >= 0){
            setUnFocus(pre);
        }

        switch(key){

            case R.id.nav_item1:
                mainToolbarTittle.setText(TITTLE_WEATHER);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_fragment_container, weatherFragment, WeatherFragment.TAG).commit();

                setOnFocus(navItem1Img,navItem1Txt);
                pre = key;
                break;
            case R.id.nav_item2:
                mainToolbarTittle.setText(TITTLE_INFO);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_fragment_container, infoFragment, InfoFragment.TAG).commit();

                setOnFocus(navItem2Img,navItem2Txt);
                pre = key;
                break;
            case R.id.nav_item3:
                mainToolbarTittle.setText(TITTLE_STORE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_fragment_container, storeFragment, StoreFragment.TAG).commit();

                setOnFocus(navItem3Img,navItem3Txt);
                pre = key;
                break;
            case R.id.nav_item4:
                mainToolbarTittle.setText(TITTLE_SETTING);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_fragment_container, settingFragment, SettingFragment.TAG).commit();

                setOnFocus(navItem4Img,navItem4Txt);
                pre = key;
                break;

        }


    }

    private void setUnFocus(int key){
        switch(key){

            case R.id.nav_item1:
                setUnFocus(navItem1Img,navItem1Txt);
                break;
            case R.id.nav_item2:
                setUnFocus(navItem2Img,navItem2Txt);
                break;
            case R.id.nav_item3:
                setUnFocus(navItem3Img,navItem3Txt);
                break;
            case R.id.nav_item4:
                setUnFocus(navItem4Img,navItem4Txt);
                break;

        }

    }
    private void setUnFocus(ImageView img,TextView txt){
        img.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.nav_item_unFocus,null)));
        txt.setTextColor(getResources().getColor(R.color.nav_item_unFocus,null));
        txt.setVisibility(View.GONE);
    }

    private void setOnFocus(ImageView img,TextView txt){
        img.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.nav_item_onFocus,null)));
        txt.setTextColor(getResources().getColor(R.color.nav_item_onFocus,null));
        txt.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this,"SOS ACTIVATE",Toast.LENGTH_LONG).show();
        return true;
    }
}