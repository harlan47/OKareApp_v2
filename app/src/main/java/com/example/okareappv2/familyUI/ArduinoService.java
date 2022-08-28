package com.example.okareappv2.familyUI;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ArduinoService extends Service {
    private RequestQueue queue;
    Timer timer;
    String result_tem, result_hum;
    Intent intent = new Intent("fromArduinoService");

    public ArduinoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue= Volley.newRequestQueue(this);
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.schedule(new ArduinoService.updaters(),0,5000);
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
                try {
                    JSONObject json=new JSONObject(response);
                    result_tem = json.getString("field1");
                    result_hum = json.getString("field2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },error -> Toast.makeText(ArduinoService.this, "Fail to get data of Arduino = " + error, Toast.LENGTH_SHORT).show());
            queue.add(stringRequest);

            intent.putExtra("result_tem", result_tem);
            intent.putExtra("result_hum", result_hum);
            sendBroadcast(intent);
        }
    }
}