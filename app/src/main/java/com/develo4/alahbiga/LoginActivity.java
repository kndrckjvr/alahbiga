package com.develo4.alahbiga;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.develo4.alahbiga.config.TaskConfig;
import com.develo4.alahbiga.utils.SnackBarCreator;
import com.develo4.alahbiga.utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements SuperTask.TaskListener {

    private final String LOGIN_ID = "login_id";
    private ConstraintLayout rootView;
    private TextInputLayout username_til;
    private TextInputLayout password_til;
    private TextInputEditText username_et;
    private TextInputEditText password_et;
    private Button login_btn;
    private Button register_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootView = findViewById(R.id.root_view);
        username_til = findViewById(R.id.username_til);
        password_til = findViewById(R.id.password_til);
        username_et = findViewById(R.id.username_et);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                SuperTask.execute(LoginActivity.this,
                        TaskConfig.LOGIN_USER_URL,
                        "login_user",
                        "Logging in...");
            }
        });

        if(getIntent().getBooleanExtra("registerSuccess", false)) {
            SnackBarCreator.set("Registered Sucessfully! We have sent an code to your mobile number.");
            SnackBarCreator.show(rootView);
        }

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int loggedOut = getIntent().getIntExtra("logout", -1);
        int loginId = sharedPreferences.getInt(LOGIN_ID, -1);

        if (loggedOut != -1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(LOGIN_ID, -1);
            editor.apply();

            SnackBarCreator.set("You have successfully logged out.");
            SnackBarCreator.show(rootView);
        }

        if (loginId != -1 && loggedOut == -1) {
            successfulLogin(loginId, false);
        }
    }

    private void successfulLogin(int uid, boolean showMessage) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("showMessage", showMessage);
        startActivity(intent);
        finish();
    }
    private void resetErrorFields() {
        username_til.setError(null);
        username_til.setErrorEnabled(false);
        password_til.setError(null);
        password_til.setErrorEnabled(false);
    }


    @Override
    public void onTaskRespond(String json, String id) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            switch (id) {
                case "login_user":
                    if(jsonObject.getBoolean("status")) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(LOGIN_ID, jsonObject.getInt("database_id"));
                        editor.apply();
                        successfulLogin(jsonObject.getInt("database_id"), true);
                    } else {
                        resetErrorFields();
                        JSONArray jsonArray = jsonObject.getJSONArray("messages");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            if(jsonObject.has("username"))
                                username_til.setError(jsonObject.getString("username"));
                            if(jsonObject.has("password"))
                                password_til.setError(jsonObject.getString("password"));
                            if(jsonObject.has("error")) {
                                SnackBarCreator.set(jsonObject.getString("error"));
                                SnackBarCreator.show(rootView);
                            }
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
            case "login_user":
                contentValues.put("username", username_et.getText().toString());
                contentValues.put("password", password_et.getText().toString());
                break;
        }
        return contentValues;
    }
}
