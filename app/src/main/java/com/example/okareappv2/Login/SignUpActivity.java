package com.example.okareappv2.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.okareappv2.R;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    EditText nameEdt, passwordEdt, product_keyEdt;
    Button register, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initComponent();
        setOnClickListener();
    }

    public void initComponent(){
        nameEdt = findViewById(R.id.register_account);
        passwordEdt = findViewById(R.id.register_password);
        product_keyEdt = findViewById(R.id.product_key);
        register = findViewById(R.id.register_button);
        back = findViewById(R.id.back_button);
    }

    private void setOnClickListener(){
        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        register.setOnClickListener(v -> {
            if (nameEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().isEmpty() || product_keyEdt.getText().toString().isEmpty()){
                Toast.makeText(SignUpActivity.this, "註冊資料有缺漏", Toast.LENGTH_SHORT).show();
                return;
            }
            postData(nameEdt.getText().toString(), passwordEdt.getText().toString(), product_keyEdt.getText().toString());

        });
    }

    private void postData(String un, String pw, String pk){
        String url = "https://okareproserver.lionfree.net/api/v1.0.0/signUp.php";
        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject respObj = new JSONObject(response);
                int resp = respObj.getInt("response");
                String msg = respObj.getString("msg");
                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
                if (resp == 100){
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(SignUpActivity.this, "Fail to post data = " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", un);
                params.put("password", pw);
                params.put("product_key", pk);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}