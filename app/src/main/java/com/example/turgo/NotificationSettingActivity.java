package com.example.turgo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.turgo.models.MLData;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;

/**
 * User is redirected here after registering
 * here the user selects if he wants notification
 * we show the user what we think he will choose
 * done by utilizing a logistic regression model
 */
public class NotificationSettingActivity extends AppCompatActivity {
    private static final String TAG = "NotificationSettingActivity";
    private TextView tvNotificationHelpText;
    private Button btnTurnOffNotification, btnTurnOnNotification;
    int[] X_train;
    int[] y_train;
    double weight;
    double bias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        setViews();
        setBtnLogic();
        getMLInfo();
        fitModel();
        makePrediction();
    }

    /**
     * uses adjusted weight and bias to make a prediction via
     * predictSetting. then sets the text on the screen to show the prediction
     */
    private void makePrediction() {
        int choice = predictSetting(weight, bias, RegisterActivity.thisAge);
        if (choice==1) tvNotificationHelpText.setText("We think you would like to use notifications");
        else tvNotificationHelpText.setText("We think you would not like to use notifications");
    }

    /**
     * sets initial weight and bias, then adjusts them to their correct
     * value for the data using computeWeightAndBias
     */
    private void fitModel() {
        weight = 50.0;
        bias = 0.0;
        double[] weightAndBias = computeWeightAndBias(X_train, y_train, weight, bias);
        weight = weightAndBias[0];
        bias = weightAndBias[1];
    }

    /**
     * calls back4app database to get the training data
     * after getting it it sets it to X_train and y_train
     */
    private void getMLInfo() {
        ParseQuery<MLData> query = ParseQuery.getQuery(MLData.class);
        MLData mldata = new MLData();
        try {
            mldata = query.find().get(0);
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }
        X_train = mldata.getAges();
        y_train = mldata.getChoices();
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