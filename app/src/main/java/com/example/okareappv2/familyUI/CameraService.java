package com.example.okareappv2.familyUI;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class CameraService extends Service {
    private RequestQueue queue;
    Timer timer;
    SharedPreferences sharedPreferences;
    Intent intent = new Intent("fromCameraService");

    public CameraService() {
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
            String url_getUserImage = "https://okareproserver.lionfree.net/api/v1.0.0/getUserImage.php";
            StringRequest request = new StringRequest(Request.Method.POST, url_getUserImage, response -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra("result_image", response);
                }
            }, error -> Toast.makeText(CameraService.this, "Fail to post data of image in service = " + error, Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", sharedPreferences.getString("username", null));
                    return params;
                }
            };
            queue.add(request);
            sendBroadcast(intent);
        }
    }
}