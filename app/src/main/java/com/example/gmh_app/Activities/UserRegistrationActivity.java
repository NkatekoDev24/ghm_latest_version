package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.example.gmh_app.Classes.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etCellphone, etAge, etLocation, etCity, etCountry, etBusinessName, etBusinessDescription;
    private RadioGroup rgEducation, rgGender, rgBusinessName, rgNameDisplayed;
    private Spinner spinnerProvince;
    private TextView tvCombinedToc, video2;
    private Button btnSubmit;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_user_registration);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("User Registration");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etCellphone = findViewById(R.id.etCellphone);
        etAge = findViewById(R.id.etAge);
        etLocation = findViewById(R.id.etLocation);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        etBusinessName = findViewById(R.id.etBusinessName);
        etBusinessDescription = findViewById(R.id.etBusinessDescription);
        rgEducation = findViewById(R.id.rgEducation);
        rgGender = findViewById(R.id.rgGender);
        rgBusinessName = findViewById(R.id.rgBusinessName);
        rgNameDisplayed = findViewById(R.id.rgNameDisplayed);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        video2 = findViewById(R.id.video2);

        // Populate the spinner
        List<String> provinces = new ArrayList<>(Arrays.asList("Select Province", "Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Limpopo", "Mpumalanga", "Northern Cape", "North West"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinces);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(adapter);

        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 1. BASICS: Why Good Money Habits – and the Separation rule</b><br>" +
                        "Video 1: Introduction – Why good money habits?<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 2: Making a profit – and not a loss.</u></b></span><br>" +
                        "Video 3: Profit in good & bad weeks: Good decisions & avoiding losses.<br>" +
                        "Video 4: The Separation Rule – Most important for hazard avoidance."
        ));

        video2.setText(Html.fromHtml("<u>VIDEO 2</u>"));

        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                submitForm();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        String email = etEmail.getText().toString().trim();
        String password = etCellphone.getText().toString().trim();
        String age = etAge.getText().toString().trim();

        if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
            showDialog("Validation Error", "Username is required.");
            isValid = false;
        } else if (TextUtils.isEmpty(email)) {
            showDialog("Validation Error", "Email is required.");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showDialog("Validation Error", "Please enter a valid email address.");
            isValid = false;
        } else if (TextUtils.isEmpty(password)) {
            showDialog("Validation Error", "Cellphone number is required.");
            isValid = false;
        } else if (!isValidSouthAfricanCellNumber(password)) {
            showDialog("Validation Error", "Please enter a valid South African cellphone number starting with +27 or 0.");
            isValid = false;
        } else if (spinnerProvince.getSelectedItemPosition() == 0) {
            showDialog("Validation Error", "Please select a province.");
            isValid = false;
        } else if (rgGender.getCheckedRadioButtonId() == -1) {
            showDialog("Validation Error", "Please select a gender.");
            isValid = false;
        } else if (rgEducation.getCheckedRadioButtonId() == -1) {
            showDialog("Validation Error", "Please select your education level.");
            isValid = false;
        } else if (rgBusinessName.getCheckedRadioButtonId() == -1) {
            showDialog("Validation Error", "Please select if you have a business name.");
            isValid = false;
        } else if (rgNameDisplayed.getCheckedRadioButtonId() == -1) {
            showDialog("Validation Error", "Please select if your name should be displayed.");
            isValid = false;
        } else if( TextUtils.isEmpty(age) || !isAge(age)) {
            showDialog("Validation Error", "Please enter an age between 14 and 120 years.");
            isValid = false;
        }
        return isValid;
    }

    private void submitForm() {
        // Collect data
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String cellphone = etCellphone.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String businessName = etBusinessName.getText().toString().trim();
        String businessDescription = etBusinessDescription.getText().toString().trim();
        String province = spinnerProvince.getSelectedItem().toString();

        // Get selected options
        String gender = getSelectedRadioText(rgGender);
        String education = getSelectedRadioText(rgEducation);
        String hasBusinessName = getSelectedRadioText(rgBusinessName);
        String isNameDisplayed = getSelectedRadioText(rgNameDisplayed);

        // Create user object
        User user = new User(username, email, cellphone, age, city, country, businessName, businessDescription, gender, education, hasBusinessName, isNameDisplayed, province);

        // Store in Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showDialogWithAction("Success", "Registration successful!", true);
            } else {
                showDialog("Error", "Failed to register. Try again!");
            }
        });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return "Not Selected";
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton.getText().toString();
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDialogWithAction(String title, String message, boolean clearForm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (clearForm) {
                        clearForm();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();
    }

    private void clearForm() {
        etUsername.setText("");
        etEmail.setText("");
        etCellphone.setText("");
        etAge.setText("");
        etCity.setText("");
        etCountry.setText("");
        etBusinessName.setText("");
        etBusinessDescription.setText("");
        rgEducation.clearCheck();
        rgGender.clearCheck();
        rgBusinessName.clearCheck();
        rgNameDisplayed.clearCheck();
        spinnerProvince.setSelection(0);
    }
    // Validation helper method
    private boolean isValidSouthAfricanCellNumber(String password) {
        //Check if the number starts with +27 or 0
        if (password.startsWith("+27")) {
            //Validate length: +27XXXXXXXXX (12 characters in total)
            return password.length() == 12 && password.substring(3).matches("\\d+");
        } else if (password.startsWith("0")) {
            //Validate length: 0XXXXXXXXX (10 characters in total)
            return password.length() == 10 && password.substring(1).matches("\\d+");
        } else {
            //Invalid prefix
            return false;
        }
    }
    private boolean isAge(String str) {
        int noAge = Integer.parseInt(str);
        return noAge > 13 && noAge < 121;
    }
}


