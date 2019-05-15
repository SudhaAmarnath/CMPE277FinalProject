package com.android.clim8;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class AirQualityActivity extends AppCompatActivity {
    String airURL = "";
    RequestQueue requestQueue;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    String currentDate="";
    String APIKEY="DCB7D8FB-3397-4243-A5A6-E6C4B7DAA287";
    EditText zipCodeeditText;
    TextView areaTextView, AqiTextMsgView, statusTextView;
    TextView TextAirquality;
    CustomGauge airQualityGauge;
    TextView TextAirqualityMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);
        zipCodeeditText = (EditText) findViewById(R.id.zipCodeeditText);
        areaTextView = (TextView) findViewById(R.id.areaTextView);
        AqiTextMsgView = (TextView) findViewById(R.id.aqiTextmsg);
        requestQueue = Volley.newRequestQueue(this);
        currentDate = sdf.format(date);
        airQualityGauge = (CustomGauge)findViewById(R.id.airQualityGauge);
        TextAirquality = (TextView) findViewById(R.id.textAirquality);
        TextAirqualityMsg = (TextView) findViewById(R.id.textAirqualityMsg);
        airQualityGauge.setEndValue(500);
        Log.d("URL", airURL);
    }


    public void airQuality(View view) {
        String zipcode=zipCodeeditText.getText().toString();
        airURL = "http://www.airnowapi.org/aq/forecast/zipCode/?format=application/json&zipCode="+zipcode+"&date=" + currentDate + "&distance=25&API_KEY=" + APIKEY;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, airURL, (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Do something with response
                //mTextView.setText(response.toString());

                // Process the JSON
                try {
                    // Loop through the array elements

                        // Get current json object
                        JSONObject student = response.getJSONObject(0);

                        // Get the current student (json object) data
                        String reportingArea = student.getString("ReportingArea");
                        Log.d("ReportingArea",reportingArea);
                        String AQI = student.getString("AQI");
                        JSONObject obj = student.getJSONObject("Category");
                        String status = obj.getString("Name");
                        // Display the formatted json data in text view
                        areaTextView.setText(reportingArea);
                        AqiTextMsgView.setText(status);
                        airQualityGauge.setValue(Integer.parseInt(AQI));
                        //airQualityGauge.setPointStartColor();
                        //airQualityGauge.setPointStartColor();
                        TextAirquality.setText("AQI : " + AQI);
                        TextAirqualityMsg.setText(getAirqualityString(Integer.parseInt(AQI)));

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }


    public String getAirqualityString (int aqi) {

        String aqiString = "";

        if (aqi == 0) {
            return aqiString;
        }
        if (aqi <= 50) {
            aqiString = "Air quality is considered satisfactory, and air pollution poses little or no risk.";
        } else if (aqi > 50 && aqi <= 100) {
            aqiString = "Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.";
        } else if (aqi > 100  && aqi <= 150) {
            aqiString = "Members of sensitive groups may experience health effects. The general public is not likely to be affected.";
        } else if (aqi > 151 && aqi <= 200) {
            aqiString = "Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects.";
        }  else if (aqi > 201 && aqi <= 250) {
            aqiString = "Health alert: everyone may experience more serious health effects.";
        } else {
            aqiString = "Health warnings of emergency conditions. The entire population is more likely to be affected.";
        }
        return aqiString;
    }

}
