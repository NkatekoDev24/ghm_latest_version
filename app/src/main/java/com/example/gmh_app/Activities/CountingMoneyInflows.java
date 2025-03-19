package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CountingMoneyInflows extends AppCompatActivity {
    private static final String TAG = "CountingMoneyInflows";

    // UI Components
    private RadioGroup personalBankAccountGroup, businessBankAccountGroup, calculateProfitLossGroup,
            regularlyCalculateProfitGroup, saveProfitGroup, emergencyFundGroup;
    private EditText profitInput;
    private TextView tvCombinedToc, intro_video_6;
    private Button submitButton;

    // Firebase Database Reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_counting_money_inflows);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 6 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI Components
        personalBankAccountGroup = findViewById(R.id.personal_bank_account_group);
        businessBankAccountGroup = findViewById(R.id.business_bank_account_group);
        calculateProfitLossGroup = findViewById(R.id.calculate_profit_loss_group);
        regularlyCalculateProfitGroup = findViewById(R.id.regularly_calculate_profit_group);
        saveProfitGroup = findViewById(R.id.save_profit_group);
        emergencyFundGroup = findViewById(R.id.emergency_fund_group);
        profitInput = findViewById(R.id.profit_input);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        intro_video_6 = findViewById(R.id.intro_video_6);
        submitButton = findViewById(R.id.submit_button);

        // Set Text for TextViews
        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 2. Counting and Recording Money INFLOWS</b><br>" +
                        "Video 5: Daily steps to count Money Inflows correctly – and avoid financial hazards.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 6: More hazards when counting daily money inflows.</b></u></span><br>" +
                        "Video 7: Correcting daily calculations of money inflows for purchases and hazards.<br>" +
                        "Video 8: Using transaction values to calculate money inflows for a day and week."
        ));

        intro_video_6.setText(Html.fromHtml("<u>VIDEO 6</u>"));

        // Set up Submit Button listener
        submitButton.setOnClickListener(v -> submitCountingMoneyInflowsData());
    }

    private void submitCountingMoneyInflowsData() {
        // Collect data from RadioGroups
        String personalBankAccount = getSelectedRadioText(personalBankAccountGroup);
        String businessBankAccount = getSelectedRadioText(businessBankAccountGroup);
        String calculateProfitLoss = getSelectedRadioText(calculateProfitLossGroup);
        String regularlyCalculateProfit = getSelectedRadioText(regularlyCalculateProfitGroup);
        String saveProfit = getSelectedRadioText(saveProfitGroup);
        String emergencyFund = getSelectedRadioText(emergencyFundGroup);
        String profit = profitInput.getText().toString().trim();

        // Validation rules
        if (personalBankAccount == null) {
            showErrorDialog("Please select an option for 'Personal Bank Account'.");
            return;
        }

        if (businessBankAccount == null) {
            showErrorDialog("Please select an option for 'Business Bank Account'.");
            return;
        }

        if (calculateProfitLoss == null) {
            showErrorDialog("Please select an option for 'Calculate Profit or Loss'.");
            return;
        }

        if (regularlyCalculateProfit == null) {
            showErrorDialog("Please select an option for 'Regularly Calculate Profit'.");
            return;
        }

        if (saveProfit == null) {
            showErrorDialog("Please select an option for 'Save Profit'.");
            return;
        }

        if (emergencyFund == null) {
            showErrorDialog("Please select an option for 'Emergency Fund'.");
            return;
        }

        if (profit.isEmpty()) {
            showErrorDialog("Please enter a profit amount.");
            return;
        }

        if (!isNumeric(profit)) {
            showErrorDialog("Profit must be a valid number.");
            return;
        }

        if (Double.parseDouble(profit) <= 0) {
            showErrorDialog("Profit must be a positive number.");
            return;
        }

        // Prepare data for Firebase
        Map<String, Object> countingMoneyInflowsData = new HashMap<>();
        countingMoneyInflowsData.put("personalBankAccount", personalBankAccount);
        countingMoneyInflowsData.put("businessBankAccount", businessBankAccount);
        countingMoneyInflowsData.put("calculateProfitLoss", calculateProfitLoss);
        countingMoneyInflowsData.put("regularlyCalculateProfit", regularlyCalculateProfit);
        countingMoneyInflowsData.put("saveProfit", saveProfit);
        countingMoneyInflowsData.put("emergencyFund", emergencyFund);
        countingMoneyInflowsData.put("profit", profit);

        // Save data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(countingMoneyInflowsData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Error submitting data: " + error);
                        showErrorDialog("Error submitting data: " + error);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    // Helper method to get the selected RadioButton text from a RadioGroup
    private String getSelectedRadioText(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        return null;
    }

    // Helper method to check if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to show an error dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Validation Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // Helper method to show success dialog
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Data submitted successfully!")
                .setPositiveButton("OK", (dialog, which) -> {
                    setResult(RESULT_OK);
                    finish(); // Close activity
                })
                .show();
    }
}
