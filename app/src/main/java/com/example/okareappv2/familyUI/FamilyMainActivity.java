package com.example.okareappv2.familyUI;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.okareappv2.LogoutButton;
import com.example.okareappv2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FamilyMainActivity extends AppCompatActivity implements View.OnClickListener {
    DrawerLayout drawerLayout;
    EditText ip;
    LogoutButton logoutButton;
    ImageView imageView, camera_picture;
    TextView temperature, humidity, signal_time, signal_result, title, username;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Switch notification, thermometer;
//    WebView webview;
//    WebSettings webSettings;
    RequestQueue queue;
    Intent intent_CameraService, intent_ArduinoService;
    CameraReceiver cameraReceiver;
    ArduinoReceiver arduinoReceiver;
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
        startService(intent_CameraService);
        cameraReceiver = new CameraReceiver();
        IntentFilter filter_camera = new IntentFilter("fromCameraService");
        registerReceiver(cameraReceiver,filter_camera);

        if (setting_thermometer){
            startService(intent_ArduinoService);
            arduinoReceiver = new ArduinoReceiver();
            IntentFilter filter_arduino = new IntentFilter("fromArduinoService");
            registerReceiver(arduinoReceiver,filter_arduino);
        }
    }

    public void onStop() {
        super.onStop();
        unregisterReceiver(cameraReceiver);
        stopService(intent_CameraService);

        if (setting_thermometer){
            unregisterReceiver(arduinoReceiver);
            stopService(intent_ArduinoService);
        }
    }

    @SuppressLint("NonConstantResourceId")
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
        notification = findViewById(R.id.notification);
        thermometer = findViewById(R.id.thermometer);
        camera_picture = findViewById(R.id.camera_view);
        ip = findViewById(R.id.ipaddress);
        title = findViewById(R.id.okare_title);
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        intent_CameraService = new Intent(FamilyMainActivity.this, CameraService.class);
        intent_ArduinoService = new Intent(FamilyMainActivity.this, ArduinoService.class);
        queue = Volley.newRequestQueue(FamilyMainActivity.this);
    }

    private void initSetting(){
        setting_notification = sharedPreferences.getBoolean("notification", true);
        setting_thermometer = sharedPreferences.getBoolean("thermometer", true);
        uName = sharedPreferences.getString("username", null);
        pKey = sharedPreferences.getString("product_key", null);

        username.setText(uName);
        notification.setChecked(setting_notification);
        thermometer.setChecked(setting_thermometer);
        if(!setting_thermometer){
            temperature.setText("??????");
            humidity.setText("??????");
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
                Toast.makeText(FamilyMainActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            }
            else{
                editor.putBoolean("notification",false);
                editor.commit();
                Toast.makeText(FamilyMainActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
        thermometer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                startService(intent_ArduinoService);
                arduinoReceiver = new ArduinoReceiver();
                IntentFilter filter_arduino = new IntentFilter("fromArduinoService");
                registerReceiver(arduinoReceiver,filter_arduino);
                Toast.makeText(FamilyMainActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                editor.putBoolean("thermometer",true);
                editor.commit();
            }
            else{
                unregisterReceiver(arduinoReceiver);
                stopService(intent_ArduinoService);
                temperature.setText("??????");
                humidity.setText("??????");
                Toast.makeText(FamilyMainActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                editor.putBoolean("thermometer",false);
                editor.commit();
            }
        });
    }

    //???????????????channel
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("serverMessage", "????????????????????????", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("when the elder falls, you will be notified a message.");
            NotificationManager notificationManager1 = getSystemService(NotificationManager.class);
            notificationManager1.createNotificationChannel(channel1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel2 = new NotificationChannel("elderMessage", "????????????????????????", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("when the elder press the SOS button and sent a emergency message, you will be notified a message.");
            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);
        }
    }

    // ??????Firebase???server_side??????????????????????????????
    private void serverChecker(){
        //????????????
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "serverMessage");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, FamilyMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date_time = formatter.format(new Date(System.currentTimeMillis()));

        //??????Firebase??????
        ElderSignal serverSignal = new ElderSignal(pKey+"/server_side");
        serverSignal.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((boolean) snapshot.child("user_state/is_fall").getValue() && setting_notification){
                    String date = snapshot.child("datetime/update_date").getValue(String.class);
                    String time = snapshot.child("datetime/update_time").getValue(String.class);
                    editor.putString("signal_time", date+" "+time);
                    editor.commit();

                    if((boolean) snapshot.child("user_state/voice_check/is_safe").getValue()){
                        //??????????????????
                        builder.setSmallIcon(R.drawable.okare_icon)
                                .setContentTitle("?????????????????????????????????")
                                .setContentText("???????????????????????????????????????????????????")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        notificationManagerCompat.notify(101,builder.build());
                        editor.putString("message", "???????????????????????????????????????????????????????????????????????????????????????");
                        editor.commit();
                    }
                    else{
                        builder.setSmallIcon(R.drawable.okare_icon)
                                .setContentTitle("?????????????????????????????????")
                                .setContentText("??????????????????????????????????????????????????????????????????????????????????????????")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        notificationManagerCompat.notify(101,builder.build());
                        editor.putString("message", "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                        editor.commit();
                    }
                }
                signal_time.setText(sharedPreferences.getString("signal_time", date_time));
                signal_result.setText(sharedPreferences.getString("message", "???????????????????????????"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    // ??????Firebase???older_side?????????
    private void elderChecker() {
        //????????????
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "elderMessage");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, FamilyMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //??????Firebase??????
        ElderSignal elderSignal = new ElderSignal(pKey+"/older_side");
        elderSignal.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((boolean) snapshot.child("sos").getValue()){
                    builder.setSmallIcon(R.drawable.okare_icon)
                            .setContentTitle("???????????????????????????????????????")
                            .setContentText("????????????????????????????????????????????????????????????")
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

    //??????CameraService???Intent???????????????
    private class CameraReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result_image = intent.getStringExtra("result_image");

            if (result_image == null){
                getUserImage();
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        byte[] bytes = Base64.getDecoder().decode(result_image);
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        camera_picture.setImageBitmap(bitmap);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //??????ArduinoService???Intent???????????????
    private class ArduinoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result_tem = intent.getStringExtra("result_tem");
            String result_hum = intent.getStringExtra("result_hum");

            if (result_tem == null || result_hum == null){
                getArduinoData();
            }
            else {
                temperature.setText(result_tem);
                temperature.append("???");
                humidity.setText(result_hum);
                humidity.append("%");
            }
        }
    }

    public void getArduinoData(){
        String url_arduino = "http://api.thingspeak.com/channels/1679756/feeds/last.json?api_key=CLPA3ECDNZAYXERC&timezone=Asia/Taipei";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_arduino, response -> {
            try {
                JSONObject json=new JSONObject(response);
                String result_tem = json.getString("field1");
                String result_hum = json.getString("field2");
                temperature.setText(result_tem);
                temperature.append("???");
                humidity.setText(result_hum);
                humidity.append("%");
            }catch (Exception e){
                e.printStackTrace();
            }
        },error -> Toast.makeText(FamilyMainActivity.this, "Fail to get data of Arduino = " + error, Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }

    public void getUserImage(){
        String url = "https://okareproserver.lionfree.net/api/v1.0.0/getUserImage.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    byte[] bytes = Base64.getDecoder().decode(response);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    camera_picture.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(FamilyMainActivity.this, "Fail to post data of image in main = " + error, Toast.LENGTH_SHORT).show()) {
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