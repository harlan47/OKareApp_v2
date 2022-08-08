package com.example.okareappv2;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.okareappv2.Login.LoginActivity;

public class LogoutButton {
    TextView textView;
    Activity activity;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String username, product_key;

    public LogoutButton(){

    }

    public LogoutButton(TextView textView, Activity activity) {
        this.textView = textView;
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", null);
        product_key = sharedPreferences.getString("product_key", null);

        textView.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("確定要登出？")
                .setMessage("登出將清除基本資料。\n帳戶名稱："+username+"\n產品金鑰："+product_key)
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .setPositiveButton("登出", (dialog, which) -> {
                    editor.putString("username", null);
                    editor.putString("product_key", null);
                    editor.commit();
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                })
                .show()
        );
    }

}
