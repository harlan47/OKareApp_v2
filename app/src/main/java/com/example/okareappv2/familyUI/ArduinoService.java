package com.example.okareappv2.familyUI;

import androidx.annotation.Nullable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

public class ArduinoService extends Service {
    private RequestQueue queue;
    Timer timer;
    String result,result1;

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
    }

    public int onStartCommand(Intent intent,int flags,int startID){
        timer=new Timer();
        timer.schedule(new updaters(),0,2000);
        return super.onStartCommand(intent,flags,startID);
    }

    private class updaters extends TimerTask {
        @Override
        public void run() {
            String url = "http://api.thingspeak.com/channels/1679756/feeds/last.json?api_key=CLPA3ECDNZAYXERC&timezone=Asia/Taipei";
            StringRequest stringRequest=new StringRequest(Request.Method.GET, url, response -> {
                System.out.println(response);
                parse_json(response);
            },null);
            queue.add(stringRequest);

            Intent intent =new Intent("fromService");
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