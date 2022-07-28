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

/**
 * Takes in fields, creates a user with the information
 * on those fields, goes to NotificationSettingActivity
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static double thisAge;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etAge;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        setBtnLogic();
    }

    /**
     * Takes information from fills and signs up the user
     * Takes you to NotificationSettingActivity
     */
    private void setBtnLogic() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(etUsername.getText().toString());
                user.setPassword(etPassword.getText().toString());
                thisAge = Double.valueOf(etAge.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null) {
                            Log.e(TAG, e.toString());
                            return;
                        }
                        Log.i(TAG, "going to notif?");
                        Intent i = new Intent(RegisterActivity.this, NotificationSettingActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
            }
        });
    }

    /**
     * Assigns the views of the activity to
     * its corresponding element
     */
    private void findViews() {
        etUsername = findViewById(R.id.etUsernameRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etAge = findViewById(R.id.etAgeRegister);
        btnRegister = findViewById(R.id.btnRegister);
    }
}