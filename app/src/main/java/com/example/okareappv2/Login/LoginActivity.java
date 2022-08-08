package com.example.okareappv2.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.okareappv2.R;
import com.example.okareappv2.familyUI.FamilyMainActivity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button registerButton, loginButton;
    EditText accountEditText, passwordEditText;
    RequestQueue queue;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String account_username, product_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (account_username != null && product_key != null){
            Intent intent = new Intent(this, FamilyMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void initComponent(){
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);
        accountEditText = findViewById(R.id.login_account);
        passwordEditText = findViewById(R.id.login_password);
        queue = Volley.newRequestQueue(LoginActivity.this);
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        account_username = sharedPreferences.getString("username", null);
        product_key = sharedPreferences.getString("product_key", null);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button:
                Intent intent1 = new Intent(this, SignUpActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.login_button:
                if (accountEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "帳號名稱或密碼不得為空白。", Toast.LENGTH_SHORT).show();
                    return;
                }
                postLoginData(accountEditText.getText().toString(), passwordEditText.getText().toString());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private void postLoginData(String un, String pw){
        String url = "https://okareproserver.lionfree.net/api/v1.0.0/login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject respObj = new JSONObject(response);
                int resp = respObj.getInt("response");
                String msg = respObj.getString("msg");
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                if (resp == 100){
                    editor.putString("username", un);
                    editor.commit();
                    getProductKey(un);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(LoginActivity.this, "Fail to post data = " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", un);
                params.put("password", pw);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void getProductKey(String un){
        String url = "https://okareproserver.lionfree.net/api/v1.0.0/getUserProductKey.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject respObj = new JSONObject(response);
                int resp = respObj.getInt("response");
                String msg = respObj.getString("msg");
                if (resp == 100){
                    editor.putString("product_key", msg);
                    editor.commit();
                    Intent intent2 = new Intent(this, FamilyMainActivity.class);
                    startActivity(intent2);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(LoginActivity.this, "Fail to post data = " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", un);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}