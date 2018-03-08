package com.develo4.alahbiga;

import android.content.ContentValues;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.develo4.alahbiga.config.TaskConfig;
import com.develo4.alahbiga.utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements SuperTask.TaskListener {

    private TextInputLayout name_til;
    private TextInputLayout email_til;
    private TextInputLayout contact_til;
    private TextInputLayout username_til;
    private TextInputLayout password_til;
    private TextInputEditText name_et;
    private TextInputEditText contact_et;
    private TextInputEditText email_et;
    private TextInputEditText username_et;
    private TextInputEditText password_et;
    private Button register_btn;

    private String TAG = "REGISTER_ACTIVITY_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name_til = findViewById(R.id.name_til);
        email_til = findViewById(R.id.email_til);
        contact_til = findViewById(R.id.contact_til);
        username_til = findViewById(R.id.username_til);
        password_til = findViewById(R.id.password_til);
        name_et = findViewById(R.id.name_et);
        email_et = findViewById(R.id.email_et);
        contact_et = findViewById(R.id.contact_et);
        username_et = findViewById(R.id.username_et);
        password_et = findViewById(R.id.password_et);
        register_btn = findViewById(R.id.register_btn);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SuperTask.execute(RegisterActivity.this,
                        TaskConfig.REGISTER_USER_URL,
                        "register_user",
                        "Registering user...");
            }
        });
    }

    private void resetErrorFields() {
        name_til.setError(null);
        name_til.setErrorEnabled(false);
        contact_til.setError(null);
        contact_til.setErrorEnabled(false);
        email_til.setError(null);
        email_til.setErrorEnabled(false);
        username_til.setError(null);
        username_til.setErrorEnabled(false);
        password_til.setError(null);
        password_til.setErrorEnabled(false);
    }

    @Override
    public void onTaskRespond(String json, String id) {
        Log.d(TAG, json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            switch (id) {
                case "register_user":
                    if(jsonObject.getBoolean("status")) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("registerSuccess", true);
                            startActivity(intent);
                            finish();
                        } else {
                            resetErrorFields();
                            JSONArray jsonArray = jsonObject.getJSONArray("messages");
                            for(int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                if(jsonObject.has("name"))
                                    name_til.setError(jsonObject.getString("name"));
                                if(jsonObject.has("email"))
                                    email_til.setError(jsonObject.getString("email"));
                                if(jsonObject.has("username"))
                                    username_til.setError(jsonObject.getString("username"));
                                if(jsonObject.has("password"))
                                    password_til.setError(jsonObject.getString("password"));
                                if(jsonObject.has("contact"))
                                    contact_til.setError(jsonObject.getString("contact"));
                            }
                    }
                    break;
            }
        } catch (Exception e) { }
    }

    @Override
    public ContentValues setRequestValues(ContentValues contentValues, String id) {
        contentValues.put("android", 1);
        switch (id) {
            case "register_user":
                contentValues.put("name", name_et.getText().toString());
                contentValues.put("email", email_et.getText().toString());
                contentValues.put("username", username_et.getText().toString());
                contentValues.put("password", password_et.getText().toString());
                contentValues.put("contact", contact_et.getText().toString());
                break;
        }
        return contentValues;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
