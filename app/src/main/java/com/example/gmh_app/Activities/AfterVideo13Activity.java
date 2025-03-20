package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AfterVideo13Activity extends AppCompatActivity {

    private RatingBar ratingBarVideo;
    private RatingBar ratingBarClarity;
    private RatingBar ratingBarUsefulness;
    private EditText editTextLesson;
    private RadioGroup radioGroupGrossNetProfit;
    private RadioGroup radioGroupWhenToUseProfit;
    private EditText editTextUnderstanding;
    private EditText editTextComments;
    private Button buttonSubmit;

    private DatabaseReference databaseReference;
    private static final String TAG = "AfterVideo13Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_video13);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 13");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Link UI elements to their corresponding views
        ratingBarVideo = findViewById(R.id.ratingBarVideo);
        ratingBarClarity = findViewById(R.id.ratingBarClarity);
        ratingBarUsefulness = findViewById(R.id.ratingBarUsefulness);
        editTextLesson = findViewById(R.id.editTextLesson);
        radioGroupGrossNetProfit = findViewById(R.id.radioGroupGrossNetProfit);
        radioGroupWhenToUseProfit = findViewById(R.id.radioGroupWhenToUseProfit);
        editTextUnderstanding = findViewById(R.id.editTextUnderstanding);
        editTextComments = findViewById(R.id.editTextComments);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(v -> validateAndSubmitFeedback());
    }

    private void validateAndSubmitFeedback() {
        // Collect data from UI elements
        float videoRating = ratingBarVideo.getRating();
        float clarityRating = ratingBarClarity.getRating();
        float usefulnessRating = ratingBarUsefulness.getRating();
        String lessonLearned = editTextLesson.getText().toString().trim();
        String understandingBenefit = editTextUnderstanding.getText().toString().trim();
        String comments = editTextComments.getText().toString().trim();

        int selectedGrossNetProfitId = radioGroupGrossNetProfit.getCheckedRadioButtonId();
        boolean understandsGrossNetProfit = (selectedGrossNetProfitId == R.id.radioYesGrossNetProfit);

        int selectedWhenToUseProfitId = radioGroupWhenToUseProfit.getCheckedRadioButtonId();
        boolean understandsWhenToUseProfit = (selectedWhenToUseProfitId == R.id.radioYesWhenToUseProfit);

        // Validation
        if (videoRating == 0 || clarityRating == 0 || usefulnessRating == 0 ||
                lessonLearned.isEmpty() || understandingBenefit.isEmpty() || comments.isEmpty()) {
            showMessageDialog("Validation Error", "Please complete all required fields before submitting.", false);
            return;
        }

        // Create a feedback map to send to Firebase
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("understandsGrossNetProfit", understandsGrossNetProfit);
        feedback.put("understandsWhenToUseProfit", understandsWhenToUseProfit);
        feedback.put("understandingBenefit", understandingBenefit);
        feedback.put("comments", comments);

        // Send data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Feedback submitted successfully!", true);
                    } else {
                        // Handle the error
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit feedback: " + error);
                        showMessageDialog("Error", "Error submitting feedback: " + error, false);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    // Helper method to show a message dialog
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
