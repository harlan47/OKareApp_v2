package com.example.okareappv2.familyUI;

import androidx.annotation.Nullable;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

public class ArduinoService extends Service {
    private RequestQueue queue;
    Timer timer;
    String result,result1;
    SharedPreferences sharedPreferences;
    Intent intent = new Intent("fromService");

    public ArduinoService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue= Volley.newRequestQueue(this);
        timer = new Timer();
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.schedule(new updaters(),0,13000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private class updaters extends TimerTask {
        @Override
        public void run() {
            String url_arduino = "http://api.thingspeak.com/channels/1679756/feeds/last.json?api_key=CLPA3ECDNZAYXERC&timezone=Asia/Taipei";
            StringRequest stringRequest=new StringRequest(Request.Method.GET, url_arduino, response -> {
                parse_json(response);
            },error -> Toast.makeText(ArduinoService.this, "Fail to get data of Arduino = " + error, Toast.LENGTH_SHORT).show());
            queue.add(stringRequest);


            String url_getUserImage = "https://okareproserver.lionfree.net/api/v1.0.0/getUserImage.php";
            StringRequest request = new StringRequest(Request.Method.POST, url_getUserImage, response -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra("result2", response);
                }
            }, error -> Toast.makeText(ArduinoService.this, "Fail to post data of image in service = " + error, Toast.LENGTH_SHORT).show()) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", sharedPreferences.getString("username", null));
                    return params;
                }
            };
            queue.add(request);

            intent.putExtra("result",result);
            intent.putExtra("result1",result1);
            sendBroadcast(intent);
        }
    }

    private void parse_json(String aa){
        try {
            JSONObject json=new JSONObject(aa);
            result=json.getString("field1");
            result1=json.getString("field2");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}