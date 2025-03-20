package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AfterVideo12Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo12Activity";

    // UI Components
    private RatingBar ratingVideo, ratingClarity, ratingUsefulness;
    private RadioGroup profitChangesGroup, profitDifferenceGroup, profitConfidenceGroup;
    private EditText lessonLearnedEditText, changesExplanationEditText, profitAmountEditText, additionalCommentsEditText;
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
        setContentView(R.layout.activity_after_video12);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 12");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        profitChangesGroup = findViewById(R.id.profit_changes);
        profitDifferenceGroup = findViewById(R.id.profit_difference);
        profitConfidenceGroup = findViewById(R.id.profit_confidence);
        lessonLearnedEditText = findViewById(R.id.lesson_learned);
        changesExplanationEditText = findViewById(R.id.changes_explanation);
        profitAmountEditText = findViewById(R.id.profit_amount);
        additionalCommentsEditText = findViewById(R.id.additional_comments);
        submitButton = findViewById(R.id.submit_button);

        // Submit button click handler
        submitButton.setOnClickListener(v -> validateAndSubmitFeedback());
    }

    private void validateAndSubmitFeedback() {
        // Collect inputs
        float videoRating = ratingVideo.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        String lessonLearned = lessonLearnedEditText.getText().toString();
        String changesExplanation = changesExplanationEditText.getText().toString();
        String profitAmount = profitAmountEditText.getText().toString();
        String additionalComments = additionalCommentsEditText.getText().toString();

        // Get selected radio button values
        String profitChanges = getSelectedRadioValue(profitChangesGroup);
        String profitDifference = getSelectedRadioValue(profitDifferenceGroup);
        String profitConfidence = getSelectedRadioValue(profitConfidenceGroup);

        // Validation
        if (videoRating == 0 || clarityRating == 0 || usefulnessRating == 0 ||
                TextUtils.isEmpty(lessonLearned) || TextUtils.isEmpty(profitChanges) || TextUtils.isEmpty(profitConfidence)) {
            showErrorDialog("Please complete all required fields before submitting.");
            return;
        }

        if (profitChanges.equals("Yes") && TextUtils.isEmpty(changesExplanation)) {
            showErrorDialog("Please explain the changes you want to make to your profits.");
            return;
        }

        if (profitDifference.equals("Yes") && TextUtils.isEmpty(profitAmount)) {
            showErrorDialog("Please provide an approximate profit amount if you expect it to change.");
            return;
        }

        // Prepare feedback data
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("profitChanges", profitChanges);
        feedback.put("changesExplanation", TextUtils.isEmpty(changesExplanation) ? "No changes explained" : changesExplanation);
        feedback.put("profitDifference", profitDifference);
        feedback.put("profitAmount", TextUtils.isEmpty(profitAmount) ? "Not provided" : profitAmount);
        feedback.put("profitConfidence", profitConfidence);
        feedback.put("additionalComments", TextUtils.isEmpty(additionalComments) ? "No comments provided" : additionalComments);

        // Save feedback to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Feedback submitted successfully!", true);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit feedback: " + error);
                        showMessageDialog("Error", "Error submitting feedback: " + error, false);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    // Helper method to get selected RadioButton value
    private String getSelectedRadioValue(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedButton = findViewById(selectedId);
            return selectedButton.getText().toString();
        }
        return "";
    }

    // Helper method to show error dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Validation Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // Helper method to show success/error message
    private void showMessageDialog(String title, String message, boolean isSuccess) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        setResult(RESULT_OK);
                        finish(); // Close activity after successful submission
                    }
                })
                .show();
    }
}
