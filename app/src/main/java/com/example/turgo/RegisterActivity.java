package com.example.turgo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(etUsername.getText().toString());
                user.setPassword(etPassword.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null) {
                            Log.e(TAG, e.toString());
                            return;
                        }
                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        });
    }

    private void findViews() {
        etUsername = findViewById(R.id.etUsernameRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
    }
}