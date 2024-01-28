package com.example.firebaseandmaps_daniel_and_venkat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText editTextBillAmount;
    private Spinner spinnerTipPercentage;
    private Button buttonCalculateTip, buttonSwitchTheme, buttonClearAll, buttonLogout;
    private TextView textViewResult, textViewUserDetails;


    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the saved theme before setting the content view
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        setTheme(getSavedTheme());

        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        editTextBillAmount = findViewById(R.id.editTextBillAmount);
        spinnerTipPercentage = findViewById(R.id.spinnerTipPercentage);
        buttonCalculateTip = findViewById(R.id.buttonCalculateTip);
        textViewResult = findViewById(R.id.textViewResult);
        buttonSwitchTheme = findViewById(R.id.buttonSwitchTheme);
        buttonClearAll = findViewById(R.id.buttonClearAll);
        buttonLogout = findViewById(R.id.logout);
        textViewUserDetails = findViewById(R.id.user_details);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tip_percentages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipPercentage.setAdapter(adapter);
        spinnerTipPercentage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        buttonCalculateTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTip();
            }
        });

        buttonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        buttonSwitchTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTheme();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textViewUserDetails.setText(user.getEmail());
        }
    }

    private void calculateTip() {
        String billAmountStr = editTextBillAmount.getText().toString();
        String tipPercentageStr = spinnerTipPercentage.getSelectedItem().toString();

        if (billAmountStr.isEmpty()) {
            textViewResult.setText("Please enter the bill amount.");
            return;
        }

        double billAmount = Double.parseDouble(billAmountStr);
        double tipPercentage = Double.parseDouble(tipPercentageStr.replace("%", ""));

        double tipAmount = billAmount * (tipPercentage / 100);
        double totalAmount = billAmount + tipAmount;

        textViewResult.setText(String.format("Tip Amount: €%.2f\nTotal Amount: €%.2f", tipAmount, totalAmount));
    }

    private void clearFields() {
        editTextBillAmount.getText().clear();
        textViewResult.setText("");
        spinnerTipPercentage.setSelection(0);
    }

    private void switchTheme() {
        int currentTheme = getSavedTheme();
        int newTheme;

        if (currentTheme == R.style.AppTheme_Light) {
            newTheme = R.style.AppTheme_Dark;
        } else if (currentTheme == R.style.AppTheme_Dark) {
            newTheme = R.style.AppTheme_Graphic;
        } else {
            newTheme = R.style.AppTheme_Light;
        }

        saveTheme(newTheme);
        recreate(); // Recreate the activity to apply the new theme
    }

    private int getSavedTheme() {
        return sharedPreferences.getInt("Theme", R.style.AppTheme_Light);
    }

    private void saveTheme(int theme) {
        sharedPreferences.edit().putInt("Theme", theme).apply();
    }
}
