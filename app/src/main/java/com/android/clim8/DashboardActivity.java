package com.android.clim8;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.nitri.gauge.Gauge;
import eu.sergehelfrich.ersa.Dew;
import eu.sergehelfrich.ersa.Scale;
import eu.sergehelfrich.ersa.Temperature;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String LOG_TAG = DashboardActivity.class.getCanonicalName();

    String ipAddress;
    String threshold;

    TextView TextHeatIndex;
    TextView TextTemperature;
    TextView TextHeatIndexMsg;
    TextView TextHumidityMsg;
    TextView TextDewPointMsg;
    TextView TextHumidity;
    TextView TextFarhenheit;
    TextView TextDewPoint;
    TextView TextToday;
    EditText ipeditText;
    EditText thresholdeditText;

    CustomGauge TemperatureGauge;
    CustomGauge HumidityGauge;
    CustomGauge HeatIndexGauge;
    //Gauge FarenheitGauge;

    Handler weatherMontitorHandler;

    // AWS IOT parameters

    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "ENTER_CUSTOMER_SPECIFIC_ENDPOINT";
    private static final String COGNITO_POOL_ID = "ENTER_COGNITO_POOL_ID";
    private static final Regions MY_REGION = Regions.US_EAST_1;//CHANGE REGION ACCORDINGLY
    private static final String topic = "ENTER_TOPIC";

    public static String OUT = "";
    public static String TEMPERATURE = "0.0";
    public static String HUMIDITY = "0.0";
    public static String HEATINDEX = "0.0";
    public static String FAHRENHEIT = "0.0";

    AWSIotMqttManager mqttManager;
    String clientId;
    CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ipeditText = (EditText) findViewById(R.id.ipeditText);
        //thresholdeditText = (EditText) findViewById(R.id.thresholdeditText);


        // Start the handler
        this.weatherMontitorHandler = new Handler();

        TextHeatIndex = (TextView) findViewById(R.id.textHeatIndex);
        TextHumidity = (TextView) findViewById(R.id.textHumidity);
        TextFarhenheit = (TextView) findViewById(R.id.textFarenheit);
        TextTemperature = (TextView) findViewById(R.id.textTemerature);
        TextDewPoint = (TextView) findViewById(R.id.textDewPoint);
        TemperatureGauge = (CustomGauge) findViewById(R.id.temperatureGauge);
        TextHeatIndexMsg = (TextView) findViewById(R.id.textHeatIndexMsg);
        TextHumidityMsg = (TextView) findViewById(R.id.textHumidityMsg);
        TextDewPointMsg = (TextView) findViewById(R.id.textDewPointMsg);
        TextToday = (TextView) findViewById(R.id.textToday);
        //FarenheitGauge = (Gauge) findViewById(R.id.farenheitGauge);

        clientId = UUID.randomUUID().toString();

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                MY_REGION
        );

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        Log.d(LOG_TAG, "clientId = " + clientId);


        weatherMonitorRunnable.run();
        new WeatherMonitorAsyncTask().execute();

        Date now = new Date();

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEE, MMM d");
        String dateStr = simpleDateformat.format(now);

        TextToday.setText(dateStr);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_exit) {
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.thresholdId) {
            threshold();
            return true;

        }  else if (id == R.id.analyticsId) {
            Intent analyticsIntent = new Intent(this,AnalyticsActivity.class);
            startActivity(analyticsIntent);

        }
        else if (id == R.id.airqualityId) {
            Intent airqualityIntent = new Intent(this, AirQualityActivity.class);
            startActivity(airqualityIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private final Runnable weatherMonitorRunnable = new Runnable()
    {
        public void run()
        {
            new WeatherMonitorAsyncTask().execute();
            DashboardActivity.this.weatherMontitorHandler.postDelayed(weatherMonitorRunnable,2000);
        }

    };


    public class WeatherMonitorAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            try {

                Thread.sleep(2000);

                mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                    @Override
                    public void onStatusChanged(final AWSIotMqttClientStatus status, final Throwable throwable) {
                        Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status == AWSIotMqttClientStatus.Connecting) {
                                    Log.d(LOG_TAG, "Connecting");

                                } else if (status == AWSIotMqttClientStatus.Connected) {
                                    Log.d(LOG_TAG, "Connected");

                                } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                    if (throwable != null) {
                                        Log.e(LOG_TAG, "Connection error.", throwable);
                                    }
                                } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                    if (throwable != null) {
                                        Log.e(LOG_TAG, "Connection error.", throwable);
                                        throwable.printStackTrace();
                                    }
                                    Log.d(LOG_TAG, "Disconnected");
                                } else {
                                    Log.d(LOG_TAG, "Disconnected");
                                }
                            }
                        });
                    }
                });
            } catch (final Exception e) {
                Log.e(LOG_TAG, "Connection error.", e);
            }

        }

        @Override
        public String doInBackground(Void... params) {

            JSONObject jObject;

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(LOG_TAG, "topic = " + topic);

            try {

                mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                        new AWSIotMqttNewMessageCallback() {
                            @Override
                            public void onMessageArrived(final String topic, final byte[] data) {
                                try {
                                    String message = new String(data, "UTF-8");
                                    setCurrentSensorValues(message);
                                    Log.d(LOG_TAG, "Message received from AWS IoT: " + message);
                                } catch (UnsupportedEncodingException e) {
                                    Log.e(LOG_TAG, "Message encoding error: ", e);
                                }
                            }
                        });
            } catch (Exception e) {
                Log.e(LOG_TAG, "Subscription error: ", e);
            }

            return getCurrentSensorValues();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String jsonStrTemperature;
            String jsonStrHumidity;
            String jsonStrHeatIndex;
            String jsonStrFahrenheit;
            String jsonStrDewPoint;
            String textHeatIndexMsg;
            String textHumidityMsg;
            String textDewPointMsg;
            float tempIndexConvInt = 50;

            try {
                JSONObject object = new JSONObject(result);
                Log.d(LOG_TAG, "JSONObject" + object);
                jsonStrTemperature = String.valueOf(object.getInt("temperature"));
                jsonStrFahrenheit = String.valueOf(object.getInt("fahrenheit"));
                jsonStrHumidity = String.valueOf(object.getInt("humidity"));
                jsonStrHeatIndex = String.valueOf(object.getInt("heatIndex"));
                double dewPoint = calculateDewPoint(Double.parseDouble(jsonStrTemperature), Double.parseDouble(jsonStrHumidity));
                jsonStrDewPoint = String.valueOf((int) dewPoint);

                TemperatureGauge.setValue(Integer.valueOf(jsonStrTemperature));
                TextHeatIndex.setText(jsonStrHeatIndex);
                TextHumidity.setText(jsonStrHumidity);
                TextTemperature.setText(jsonStrTemperature);
                TextFarhenheit.setText(jsonStrFahrenheit);
                TextDewPoint.setText(jsonStrDewPoint);

                textHeatIndexMsg = getHeatIndexString(Integer.valueOf(jsonStrHeatIndex));
                TextHeatIndexMsg.setText(textHeatIndexMsg);

                textHumidityMsg = getHumidityString(Integer.valueOf(jsonStrHumidity));
                TextHumidityMsg.setText(textHumidityMsg);

                textDewPointMsg = getDewPointString(Integer.valueOf(jsonStrDewPoint));
                TextDewPointMsg.setText(textDewPointMsg);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void setCurrentSensorValues (String message) {

        try {
            JSONObject jsonObjectShadow = new JSONObject(message);
            JSONObject jsonObjectState =  jsonObjectShadow.getJSONObject("state");
            JSONObject jsonObjectReported =  jsonObjectState.getJSONObject("reported");
            TEMPERATURE = String.valueOf(jsonObjectReported.getDouble("tem"));
            FAHRENHEIT  = String.valueOf(jsonObjectReported.getDouble("fah"));
            HUMIDITY    = String.valueOf(jsonObjectReported.getDouble("hum"));
            HEATINDEX   = String.valueOf(jsonObjectReported.getDouble("hid"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getCurrentSensorValues() {

        // Build the final sensor string output
        OUT = "{" +
                "\"temperature\": " + TEMPERATURE + ", " +
                "\"humidity\": " + HUMIDITY + ", " +
                "\"heatIndex\": " + HEATINDEX + ", " +
                "\"fahrenheit\": " + FAHRENHEIT +
                "}";

        return OUT;
    }

    public String getHumidityString (int humidity) {

        String humidityString = "";

        if (humidity == 0) {
            return humidityString;
        }
        if (humidity >= 1 && humidity < 30) {
            humidityString = "LOW HUMIDITY : May cause dry and itchy skin, susceptibility to cold and infection, damage to wood furnitures";
        } else if (humidity > 30 && humidity <= 60) {
            humidityString = "\nOPTIMUM HUMIDITY : Comfortable";
        } else {
            humidityString = "HIGH HUMIDITY : Possible mold growth, sleep discomfort, muggy conditions";
        }
        return humidityString;
    }

    public String getHeatIndexString (int heatIndex) {

        String heatIndexString = "";

        if (heatIndex == 0) {
            return heatIndexString;
        }
        if (heatIndex <= 80) {
            heatIndexString = "\nMODERATE HEAT INDEX : Ok";
        } else if (heatIndex > 80 && heatIndex <= 90) {
            heatIndexString = "CAUTION : Fatigue possible with prolonged exposure and/or physical activity";
        } else if (heatIndex > 90  && heatIndex <= 103) {
            heatIndexString = "EXTREME CAUTION : Heat stroke, heat cramps, or heat exhaustion possible with prolonged exposure and/or physical activity";
        } else if (heatIndex > 103 && heatIndex <= 124) {
            heatIndexString = "DANGER : Heat cramps or heat exhaustion likely, and heat stroke possible with prolonged exposure and/or physical activity";
        } else {
            heatIndexString = "EXTREME DANGER : Heat stroke highly likely";
        }
        return heatIndexString;
    }

    public double calculateDewPoint (double celcius, double rh) {

        Temperature temperature = new Temperature(celcius, Scale.CELSIUS);
        Temperature dewPoint = new Temperature(Scale.CELSIUS);
        double relativeHumidity = rh;
        double dewPointC = 0;
        double dewPointF = 0;

        if (celcius == 0 || rh == 0) {
            return dewPointF;
        }

        Dew dew = new Dew();

        try {
            double kelvin = temperature.getKelvin();
            dewPoint.setKelvin(dew.dewPoint(relativeHumidity, kelvin));
            dewPointC = dewPoint.getTemperature();
            dewPointF = dewPointC * 1.8 + 32;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dewPointF;

    }

    public String getDewPointString (int dewpoint) {

        String dewPointString = "";

        if (dewpoint == 0) {
            return dewPointString;
        }

        if (dewpoint < 50) {
            dewPointString = "\nA bit dry for some";
        } else if (dewpoint >= 50 && dewpoint <= 54) {
            dewPointString = "\nVery comfortable";
        } else if (dewpoint >= 55 && dewpoint <= 59) {
            dewPointString = "\nComfortable";
        } else if (dewpoint >= 60 && dewpoint <= 64) {
            dewPointString = "\nOK for most, but all perceive the humidity at upper edge";
        } else if (dewpoint >= 65 && dewpoint <= 69) {
            dewPointString = "\nSomewhat uncomfortable for most people at upper edge";
        } else if (dewpoint >= 70 && dewpoint <= 74) {
            dewPointString = "\nVery humid, quite uncomfortable";
        } else if (dewpoint >= 75 && dewpoint <= 80) {
            dewPointString = "\nExtremely uncomfortable, oppressive";
        } else {
            dewPointString = "\nSeverely high, even deadly for asthama related illness";
        }

        return dewPointString;
    }


    public void threshold(){
        LayoutInflater layoutInflater = LayoutInflater.from(DashboardActivity.this);
        View promptView = layoutInflater.inflate(R.layout.threshold, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashboardActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText tempMaxThreshHold = (EditText) promptView.findViewById(R.id.temperatureThreshold);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences sharedPref = getSharedPreferences("ipInfoWeather", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Threshold",tempMaxThreshHold.getText().toString());
                        editor.apply();
                        thresholdeditText.setText(tempMaxThreshHold.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}