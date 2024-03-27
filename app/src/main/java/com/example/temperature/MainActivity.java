package com.example.temperature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.annotation.NonNull;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    // UI components
    private EditText latitudeInput, longitudeInput;
    private TextView temperatureDisplay;

    private LocationManager locationManager;


    private static final int PERMISSION_REQUEST_CODE = 1001; // You can use any value for the request code

    // Handler and Runnable for auto-refresh feature
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize UI components
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        temperatureDisplay = findViewById(R.id.temperatureNumber);
        Button refreshButton = findViewById(R.id.refreshButton);
        Button locationButton = findViewById(R.id.locationButton);
        Switch autoRefreshSwitch = findViewById(R.id.autoRefreshSwitch);

        // Initialize LocationManager and Handler
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        handler = new Handler(Looper.getMainLooper());

        // Set click listeners for buttons
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchTemperature();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        // Set listener for auto-refresh switch
        autoRefreshSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        });
    }
    // Get temperature from OpenWeather API
    private void fetchTemperature() {
        String latitude = latitudeInput.getText().toString();
        String longitude = longitudeInput.getText().toString();
        String API_KEY = BuildConfig.API_KEY;
        String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
        String url = BASE_URL + "?lat=" + latitude + "&lon=" + longitude + "&units=metric" + "&appid=" + API_KEY;
        Log.d("URL", url);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                showToast("Failed to fetch temperature. Check your internet connection.");
            }


            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject main = jsonObject.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        updateTemperatureDisplay(temperature);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Failed to parse temperature data.");
                    }
                }
            }
        });
    }

    // Update temperature display on the UI
    private void updateTemperatureDisplay(final double temperature) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temperatureDisplay.setText(temperature + "°C");
            }
        });
    }

    private void getLocation() {
        // Check if permission is not granted
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);

            // Return as permission is not granted yet
            return;
        }

        // If permission is already granted, proceed to get location
        setupLocationListener();
    }

    // After user responds to permission request, this method will be called
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if all requested permissions are granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, proceed to get location
                setupLocationListener();
            } else {
                // Permission is denied, show a message or handle accordingly
                showToast("Location permission denied.");
                Log.d("Location", "Location permission denied");
            }
        }
    }

    private void setupLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showToast("Cannot get location without location permissions, please turn them on!");
            return;
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitudeInput.setText(String.valueOf(location.getLatitude()));
                longitudeInput.setText(String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }
        };
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
    }

    // Display message in pop-up
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startAutoRefresh() {
        runnable = new Runnable() {
            @Override
            public void run() {
                getLocation();
                fetchTemperature();
                handler.postDelayed(this, 60000); // Refresh every minute (60000 milliseconds)
            }
        };
        handler.post(runnable);
    }

    private void stopAutoRefresh() {
        handler.removeCallbacks(runnable);
    }
}
