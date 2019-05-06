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
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String LOG_TAG = DashboardActivity.class.getCanonicalName();

    String ipAddress;
    String threshold;

    TextView TextHeatIndex;
    TextView TextTemperature;
    TextView TextDayHighValue;
    TextView TextDayLowValue;
    TextView TextHumidity;
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
        TextTemperature = (TextView) findViewById(R.id.textTemerature);
        TextDayHighValue = (TextView) findViewById(R.id.textDayHighValue);
        TextDayLowValue = (TextView) findViewById(R.id.textDayLowValue);
        TemperatureGauge = (CustomGauge) findViewById(R.id.temperatureGauge);
        HumidityGauge = (CustomGauge) findViewById(R.id.humidityGauge);
        HeatIndexGauge = (CustomGauge) findViewById(R.id.heatIndexGauge);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.ipaddressId) {

            ipAddress();
            return true;
        } else if (id == R.id.thresholdId) {
            threshold();
            return true;

        } else if (id == R.id.configurationId) {

        } else if (id == R.id.startserverId) {
            SharedPreferences sharedPref = getSharedPreferences("WeatherInfo",Context.MODE_PRIVATE);
            ipAddress = sharedPref.getString("IPAddress",null);
            threshold = sharedPref.getString("Threshold","");
            ipeditText.setText(ipAddress);
            thresholdeditText.setText(threshold);

        } else if (id == R.id.analyticsId) {
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

                Thread.sleep(1000);

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
                Thread.sleep(10000);
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
            float tempIndexConvInt = 50;

            try {
                JSONObject object = new JSONObject(result);
                Log.d(LOG_TAG, "JSONObject" + object);
                jsonStrTemperature = String.valueOf(object.getInt("temperature"));
                jsonStrFahrenheit = String.valueOf(object.getInt("fahrenheit"));
                jsonStrHumidity = String.valueOf(object.getInt("humidity"));
                jsonStrHeatIndex = String.valueOf(object.getInt("heatIndex"));

                TemperatureGauge.setValue(Integer.valueOf(jsonStrTemperature));
                HumidityGauge.setValue(Integer.valueOf(jsonStrHumidity));
                HeatIndexGauge.setValue(Integer.valueOf(jsonStrHeatIndex));

                //FarenheitGauge.setValue(Float.parseFloat((jsonStrFahrenheit)));

                TextHeatIndex.setText(jsonStrHeatIndex);
                TextHumidity.setText(jsonStrHumidity);
                TextTemperature.setText(jsonStrTemperature);
                TextDayHighValue.setText(jsonStrTemperature);
                TextDayLowValue.setText(jsonStrTemperature);

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

    public void ipAddress(){
        LayoutInflater layoutInflater = LayoutInflater.from(DashboardActivity.this);
        View promptView = layoutInflater.inflate(R.layout.ip_address, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashboardActivity.this);
        alertDialogBuilder.setView(promptView);

        final String ipRegEx = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

        final EditText ipTxtField = (EditText) promptView.findViewById(R.id.ipAddress);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Pattern patternIp = Pattern.compile(ipRegEx);
                        Matcher matcherIp = patternIp.matcher(ipTxtField.getText().toString());

                        if(matcherIp.find()){

                            SharedPreferences sharedPref = getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("IPAddress",ipTxtField.getText().toString());
                            editor.apply();

                        }else{
                            Toast.makeText(getApplicationContext(),"Invalid IP Address", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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

    public void networkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected())) {
        } else {

            Context context = getApplicationContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("No internet Connection ! are You sure to connect internet ?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                    intent.setComponent(cn);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}