package com.example.okareappv2.familyUI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.okareappv2.Login.LoginActivity;
import com.example.okareappv2.LogoutButton;
import com.example.okareappv2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FamilyMainActivity extends AppCompatActivity implements View.OnClickListener {
    DrawerLayout drawerLayout;
    EditText ip;
    LogoutButton logoutButton;
    ImageView imageView, webview;
    TextView temperature, humidity, signal_time, signal_result, title, username, product_key;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Switch notification, thermometer;
//    WebView webview;
    WebSettings webSettings;
    myReceiver myreceiver;
    String uName, pKey;
    boolean setting_notification, setting_thermometer;
    private int isShow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_main);
        initComponent();
        initSetting();
        setOnClickListener();
        setOnCheckedChangeListener();
        createNotificationChannel();
//        setWebViewByUrl("google.com");
        Thread t1 = new Thread(() -> elderChecker());
        Thread t2 = new Thread(() -> serverChecker());
        t1.start();
        t2.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onStart(){
        super.onStart();
        getUserImage();
        if (setting_thermometer)
            startArduinoService();
    }

    public void onStop() {
        super.onStop();
        if (setting_thermometer)
            this.unregisterReceiver(myreceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_icon:
                drawerLayout.open();
                break;
            case 2:
                break;
        }
    }

    private void initComponent() {
        drawerLayout = findViewById(R.id.family_main_drawer_layout);
        logoutButton = new LogoutButton(findViewById(R.id.family_logout), this);
        imageView = findViewById(R.id.drawer_icon);
        temperature = findViewById(R.id.family_temperature);
        humidity = findViewById(R.id.family_humidity);
        signal_time = findViewById(R.id.signal_time);
        signal_result = findViewById(R.id.signal_result);
        username = findViewById(R.id.account_name);
        product_key = findViewById(R.id.product_key);
        notification = findViewById(R.id.notification);
        thermometer = findViewById(R.id.thermometer);
        webview = findViewById(R.id.webcam_view);
        ip = findViewById(R.id.ipaddress);
        title = findViewById(R.id.okare_title);
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void initSetting(){
        setting_notification = sharedPreferences.getBoolean("notification", true);
        setting_thermometer = sharedPreferences.getBoolean("thermometer", true);
        uName = sharedPreferences.getString("username", null);
        pKey = sharedPreferences.getString("product_key", null);

        username.setText(uName);
        product_key.setText(pKey);
        notification.setChecked(setting_notification);
        thermometer.setChecked(setting_thermometer);
        if(!setting_thermometer){
            temperature.setText("停用");
            humidity.setText("停用");
        }
    }

    private void setOnClickListener() {
        imageView.setOnClickListener(this);
        title.setOnClickListener(v -> {
            if (isShow%2 == 1) {
                ip.setVisibility(View.VISIBLE);
            } else {
                ip.setVisibility(View.INVISIBLE);
//                setWebViewByUrl(ip.getText().toString());
            }
            isShow++;
        });
    }

    private void setOnCheckedChangeListener() {
        notification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                editor.putBoolean("notification",true);
                editor.commit();
                Toast.makeText(FamilyMainActivity.this, "推送通知已開啟", Toast.LENGTH_SHORT).show();
            }
            else{
                editor.putBoolean("notification",false);
                editor.commit();
                Toast.makeText(FamilyMainActivity.this, "推送通知已關閉", Toast.LENGTH_SHORT).show();
            }
        });
        thermometer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                editor.putBoolean("thermometer",true);
                editor.commit();
                Toast.makeText(FamilyMainActivity.this, "室內溫溼度監控已開啟。需重啟", Toast.LENGTH_SHORT).show();
            }
            else{
                editor.putBoolean("thermometer",false);
                editor.commit();
                Toast.makeText(FamilyMainActivity.this, "室內溫溼度監控已關閉。需重啟", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //設定通知的channel
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("serverMessage", "系統偵測跌倒通知", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("when the elder falls, you will be notified a message.");
            NotificationManager notificationManager1 = getSystemService(NotificationManager.class);
            notificationManager1.createNotificationChannel(channel1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel2 = new NotificationChannel("elderMessage", "家屬緊急求救通知", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("when the elder press the SOS button and sent a emergency message, you will be notified a message.");
            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);
        }
    }

    // 抓取Firebase中server_side的資料，以及推送通知
    private void serverChecker(){
        //建立通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "serverMessage");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, FamilyMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date_time = formatter.format(new Date(System.currentTimeMillis()));

        //接收Firebase資料
        ElderSignal serverSignal = new ElderSignal("RQJO-TOAZ-OKLT-VJTM/server_side");
        serverSignal.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((boolean) snapshot.child("user_state/is_fall").getValue() && setting_notification){
                    String date = snapshot.child("datetime/update_date").getValue(String.class);
                    String time = snapshot.child("datetime/update_time").getValue(String.class);
                    editor.putString("signal_time", date+" "+time);
                    editor.commit();

                    if((boolean) snapshot.child("user_state/voice_check/is_safe").getValue()){
                        //設定通知內容
                        builder.setSmallIcon(R.drawable.okare_icon)
                                .setContentTitle("系統偵測到您的家屬跌倒")
                                .setContentText("經系統確認過後並無大礙，請您放心！")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        notificationManagerCompat.notify(101,builder.build());
                        editor.putString("message", "系統偵測到您的家屬跌倒。經系統確認過後並無大礙，請您放心！");
                        editor.commit();
                    }
                    else{
                        builder.setSmallIcon(R.drawable.okare_icon)
                                .setContentTitle("系統偵測到您的家屬跌倒")
                                .setContentText("您的家屬在三分鐘內無任何回應，建議查看攝影機畫面來確認狀況！")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        notificationManagerCompat.notify(101,builder.build());
                        editor.putString("message", "系統偵測到您的家屬跌倒。且在三分鐘內無任何回應，建議查看攝影機畫面來確認狀況！");
                        editor.commit();
                    }
                }
                signal_time.setText(sharedPreferences.getString("signal_time", date_time));
                signal_result.setText(sharedPreferences.getString("message", "尚未收到任何警訊。"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    // 抓取Firebase中older_side的資料
    private void elderChecker() {
        //建立通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "elderMessage");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, FamilyMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //接收Firebase資料
        ElderSignal elderSignal = new ElderSignal("RQJO-TOAZ-OKLT-VJTM/older_side");
//        elderSignal.reference.child("sos").setValue(false);
        elderSignal.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((boolean) snapshot.child("sos").getValue()){
                    builder.setSmallIcon(R.drawable.okare_icon)
                            .setContentTitle("系統收到家屬的緊急求救訊號")
                            .setContentText("請查看攝影機畫面來確認狀況，並聯絡家屬！")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    notificationManagerCompat.notify(100,builder.build());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    //接收ArduinoService以Intent傳送的資料
    private class myReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result=intent.getStringExtra("result");
            String result1=intent.getStringExtra("result1");
            temperature.setText(result);
            temperature.append("℃");
            humidity.setText(result1);
            humidity.append("%");
        }
    }

    //啟動ArduinoService
    public void startArduinoService(){
        Intent intent = new Intent();
        intent.setClass(FamilyMainActivity.this, ArduinoService.class);
        startService(intent);
        myreceiver=new myReceiver();
        IntentFilter filter=new IntentFilter("fromService");
        registerReceiver(myreceiver,filter);
    }

    public void getUserImage(){
        String url = "https://okareproserver.lionfree.net/api/v1.0.0/getUserImage.php";
        RequestQueue queue = Volley.newRequestQueue(FamilyMainActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                byte[] bytes = Base64.getDecoder().decode(response);
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                webview.setImageBitmap(bitmap);
            }
        }, error -> Toast.makeText(FamilyMainActivity.this, "Fail to post data = " + error, Toast.LENGTH_SHORT).show()) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", uName);
                return params;
            }
        };
        queue.add(stringRequest);
    }

//    @SuppressLint("SetJavaScriptEnabled")
//    public void setWebViewByUrl(String url) {
//        webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webview.setWebViewClient(new WebViewClient());
//        webview.loadUrl(url);
//    }
}