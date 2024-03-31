package com.example.temperatureconverter.activities;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import static com.example.temperatureconverter.libs.Utils.showInfoDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.temperatureconverter.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.temperatureconverter.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import android.view.Menu;
import android.view.MenuItem;


import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Snackbar mSnackBar;
    private static final String KEY_TEMPERATURE = "temperature";
    private static final String KEY_AUTO_SAVE = "auto_save";
    private RadioGroup radioGroup;
    private RadioButton celsiusRadioButton, fahrenheitRadioButton;
    private ExtendedFloatingActionButton convertButton;
    private TextView resultTextView;
    private TextView temperatureInput;
    private boolean useAutoSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioGroup = findViewById(R.id.radioGroup);
        celsiusRadioButton = findViewById(R.id.celsiusRadioButton);
        fahrenheitRadioButton = findViewById(R.id.fahrenheitRadioButton);
        convertButton = findViewById(R.id.convertButton);
        temperatureInput = findViewById(R.id.temperatureInput);

        mSnackBar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                convertTemperature(view);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        savePreferences();
    }

    private void savePreferences() {
        SharedPreferences preferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(KEY_TEMPERATURE, temperatureInput.getText().toString());
        editor.putBoolean(KEY_AUTO_SAVE, useAutoSave);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        restoreFromPreferences();
    }

    private void restoreFromPreferences() {
        SharedPreferences preferences = getDefaultSharedPreferences(this);

        String savedTemperature = preferences.getString(KEY_TEMPERATURE, "");
        temperatureInput.setText(savedTemperature);

        useAutoSave = preferences.getBoolean(KEY_AUTO_SAVE, false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_AUTO_SAVE, useAutoSave);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        useAutoSave = savedInstanceState.getBoolean(KEY_AUTO_SAVE, false);
    }

    @SuppressLint("DefaultLocale")
    private void convertTemperature(View view) {
        try {
            double inputTemperature = Double.parseDouble(temperatureInput.getText().toString());
            double resultTemperature;

            if (celsiusRadioButton.isChecked()) {
                // Celsius to Fahrenheit
                resultTemperature = (inputTemperature * 9 / 5) + 32;
                mSnackBar.setText(String.format("%.2f°C is %.2f°F", inputTemperature, resultTemperature));
                mSnackBar.show();
            } else if (fahrenheitRadioButton.isChecked()) {
                // Fahrenheit to Celsius
                resultTemperature = (inputTemperature - 32) * 5 / 9;
                mSnackBar.setText(String.format("%.2f°F is %.2f°C", inputTemperature, resultTemperature));
                mSnackBar.show();
            } else {
                mSnackBar.setText("Please select Celsius or Fahrenheit");
                mSnackBar.show();
            }
        } catch (NumberFormatException e) {
            mSnackBar.setText("Please enter a valid temperature");
            mSnackBar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_about) {
            showAbout();
            return true;
        } else if (itemId == R.id.action_settings) {
            showSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showAbout() {
        dismissSnackBarIfShown();
        showInfoDialog(MainActivity.this, "About Temperature Converter:",
                "A quick way to convert temperatures. Enjoy!\n" +
                        "\nAndroid game by EW.");
    }
    private void showSettings() {
        dismissSnackBarIfShown();
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        settingsLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                SharedPreferences sp = getDefaultSharedPreferences(this);
                useAutoSave = sp.getBoolean(KEY_AUTO_SAVE, true);
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode ==1){
            SharedPreferences sp = getDefaultSharedPreferences(this);
            useAutoSave = sp.getBoolean(KEY_AUTO_SAVE, true);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void dismissSnackBarIfShown() {
        if (mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }
}