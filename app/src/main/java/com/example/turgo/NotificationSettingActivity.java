package com.example.turgo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParsePush;

public class NotificationSettingActivity extends AppCompatActivity {
    private static final String TAG = "NotificationSettingActivity";
    private TextView tvNotificationHelpText;
    private Button btnTurnOffNotification, btnTurnOnNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        setViews();
        setBtnLogic();

        int[] X_train = {83,86,77,93,35,86,92,49,21,62,27,90,59,63,26,40,26,72,36,68,67,29,82,30,62,23,67,35,29,22,58,69,67,93,56,42,29,73,21,19,84,37,98,24,70,26,91,80,56,73,62,70,96,81,25,84,27,36,46,29,57,24,95,82,45,67,34,64,43,50,87,76,78,88,84,51,54,99,32,60,76,68,39,26,86,94,39,95,70,34,78,67,97,92,52,56,80,86,41,65};
        int[] y_train = {1,1,1,1,0,1,1,0,0,0,0,1,0,0,0,0,0,1,0,1,1,0,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,1,0,0,1,0,1,0,1,0,1,1,0,1,0,1,1,1,0,1,0,0,0,0,0,0,1,1,0,1,0,0,0,0,1,1,1,1,1,0,0,1,0,0,1,1,0,0,1,1,0,1,1,0,1,1,1,1,0,0,1,1,0,0,};

        double weight = 50.0, bias = 0.0;
        double[] weightAndBias = computeWeightAndBias(X_train, y_train, weight, bias);
        weight = weightAndBias[0];
        bias = weightAndBias[1];

        int choice = predictSetting(weight, bias, RegisterActivity.thisAge);
        if (choice==1) tvNotificationHelpText.setText("We think you would like to use notifications");
        else tvNotificationHelpText.setText("We think you would not like to use notifications");
    }

    /**
     * sets the listener for the buttons in the view
     * Both buttons add the age and the answer to the database so that it can act as a datapoint
     * next time the model is trained
     */
    private void setBtnLogic() {
        btnTurnOnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParsePush.subscribeInBackground(ParseApplication.allUsers);
                saveDataPoint(RegisterActivity.thisAge, 1);
                goMainActivity();
            }
        });
        btnTurnOffNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataPoint(RegisterActivity.thisAge, 0);
                goMainActivity();
            }
        });
    }

    /**
     * adds datapoint to the database
     *
     * @param thisAge age of the person that just made the account
     * @param i decision, where 1 means the notification was turned on and 0 is the contrary
     */
    private void saveDataPoint(double thisAge, int i) {

    }

    /**
     * takes the user to MainActivity with an fading animation
     */
    private void goMainActivity() {
        Intent i = new Intent(NotificationSettingActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    /**
     * assigns views
     */
    private void setViews() {
        tvNotificationHelpText = findViewById(R.id.tvNotificationSettingText);
        btnTurnOffNotification = findViewById(R.id.btnTurnOffNotification);
        btnTurnOnNotification = findViewById(R.id.btnTurnOnNotification);
    }

    /**
     * Takes in weight and bias which are transformed via gradient descent in order to help make predictions
     *
     *
     * @param x_train
     * @param y_train
     * @param w weight assgined to feature, since there is only 1 feature this is a double instead of a double array
     * @param b bias for control
     * @return weight and bias used for predictions, or in simpler terms: slope and y intercept
     */
    private native double[] computeWeightAndBias(int[] x_train, int[] y_train, double w, double b);

    /**
     * Makes a prediction utilizing the weight and bias of the model.
     *
     * @param w weight for age feature
     * @param b bias of model
     * @param age age of the person who we want to make the prediction for
     * @return 1 if the model predicts the person will turn on notifications, 0 if not
     */
    private native int predictSetting(double w, double b, double age);
}