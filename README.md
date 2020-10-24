### MQTT-THsensor-AQI

# CMPE 277 SMARTPHONE APPLICATION DEVELOPMENT

# FINAL PROJECT REPORT



# Environmental Monitoring System Using IoT and Cloud Service at Real-Time




# TEAM SPARTANS

MUKESH RANJAN SAHAY
MUTHU KUMAR SUKUMARAN
SUDHA AMARNATH
THIRUMALAI NAMBI DOSS PALANI






# ABSTRACT
		 	 	 	
Monitoring environmental activities has become very crucial over the days for controlling the electronic appliances around us. With the help of a new technology stack, we have designed and developed a mobile application using the Internet of Things (IoT) based on a real-time environmental monitoring system. Sensors are connected to the cloud and the device is subscribed to data published to the cloud.From our application, we can get the weather details like Temperature, Humidity, Heat Index and Air Quality. The application also possesses the capability to send a notification to the subscribed users when the temperature reaches the threshold limit. We are using Amazon One-Click button to send a notification to the user with the temperature, humidity and heat index details. server. This is a low-cost system which gives insight into the design and implementation of a complete IoT application involving all aspects from sensing and wireless transmission to cloud storage and data retrieval from the cloud via a mobile application. The results of the project show the real-time monitoring of temperature and humidity levels from any location in the world and its statistical analysis. This system can be extended to enable remote controlling of various appliances based on the sensed data. 

We have used the NodeMCU (ESP-8266) because of its simplicity, robustness and lost cost. The system uses the WiFi module ESP8266 in order to upload sensor data from DHT-11 to the cloud data store We are using DHT11 sensor to capture the data.It guarantees a great consistency and outstanding long-term steadiness. DHT-11 sensor comprises of a resistive-type humidity measurement component and an NTC temperature measurement component. It is small sensor with fast response and high quality. It has a comparatively low cost and strong anti-interference ability, digital signal output, and precise calibration. It has a temperature range from 0 to 50°C and humidity range from 20 to 90%RH, and the signal transmission range of 20m. We have installed the DHT library and used it to get the required data. 
ARCHITECTURE


 









# TECHNOLOGIES USED

# Hardware

The below diagram depicts a picture of the NodeMCU microcontroller used in our system.

 

NodeMCU - ESP8266


	 

DHT11 - Temperature and Humidity Sensor





Significance of using DHT11 Sensor
●	3 to 5V power and I/O
●	2.5mA max current use during conversion (while requesting data)
●	Good for 20-80% humidity readings with 5% accuracy
●	Good for 0-50°C temperature readings ±2°C accuracy
●	No more than 1 Hz sampling rate (once every second)
●	Body size 15.5mm x 12mm x 5.5mm
●	4 pins with 0.1" spacing


# Software

Software used in the application
●	Arduino
●	Android Studio













# CLOUD SERVICES

Below are the cloud services used in our temperature monitoring services.
●	AWS Mobile SDK
●	AWS IoT Core
●	AWS IoT Topic
●	AWS Cognito
●	AWS IoT 1-Click
●	AWS SNS
●	AWS SES
●	AWS IoT Analytics
●	AWS Lambda
●	AWS DynamoDB
●	AWS RDS
●	AWS Mobile Hub	











# Data Model
Sensors are captured from the environment and sent to Sensor Reading table and Shadow Table. IoT notification will be initiated when the readings threshold value breaches the user defined value.


 








FLOW DIAGRAM

Below flow diagram depicts the collection of sensor data from DHT11 and send them to cloud through ESP8266 with MQTT protocol.
 









SETUP HARDWARE AND SOFTWARE MODULES

Steps to run the Arduino Code:
1.	Download Arduino software version 1.8.9.
2.	Addthis  link http://arduino.esp8266.com/stable/package_esp8266com_index.json in file preferences “New Boards”.
3.	Select Board Manager: Search ESP8266 and install SDK version 2.4.1. This would install all the NODE MCU board softwares.
4.	Select Tools Board à NodeMCU 1.0 ESP 12E Module and Baud rate 115200 and connected COM port.

Arduino Libraries installed:
1.	aws-sdk-arduino from https://github.com/odelot/aws-sdk-arduino
2.	arduinoWebSockets v.2.1.0 from https://github.com/Links2004/arduinoWebSockets/releases/tag/2.1.0
3.	pubsubclient v2.6.0 from https://github.com/knolleary/pubsubclient/releases/tag/v2.6
4.	Install DHT sensor library by Adafruit - ‘DHT sensor’. v.1.3.4
5.	Install Adafruit Unified Sensor v.1.0.3 by Adafruit

Running Arduino program:
1.	Open and the Arduino code WeatherMonitoringMetricsToAwsIotShadow.ino
2.	Update the AWS keys, IoT topic Connect the NodeMCU module to the COM port.
3.	Compile and download the code to the NodeMCU flash
 
Setup AWS IOT Core for MQTT:
1.	Setup IOT project thing and IoT topic.
2.	Setup IOT shadow.
3.	Setup AWS Cognito Identity 
Setup Andriod App:
Following AWS classes are imported from SDK library aws-android-sdk-iot:2.13.+ in the build.gradle
1.	def aws_version = "2.13.+"
2.	implementation "com.amazonaws:aws-android-sdk-iot:$aws_version"
3.	implementation ('com.amazonaws:aws-android-sdk-iot:$aws_version@aar') { transitive = true}
4.	implementation "com.amazonaws:aws-android-sdk-mobile-client:$aws_version"
 
Import following classes for AWS Cognito, and MQTT functions from AWS libraries.
 
1.	import com.amazonaws.auth.CognitoCachingCredentialsProvider;
2.	import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
3.	import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
4.	import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
5.	import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
6.	import com.amazonaws.regions.Regions;
7.	Configure AWS Region, AWS Cognito user identiy

Finally Build and Install project APK to the Android phone








REST INTERFACES AND APIs IN THE APP

// MQTT Client
AWSIotMqttManager mqttManager;
mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);
Intent analyticsIntent = new Intent(this,AnalyticsActivity.class);
Intent airqualityIntent = new Intent(this, AirQualityActivity.class);
public class WeatherMonitorAsyncTask extends AsyncTask<Void, Void, String>
  mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback()
  public String doInBackground(Void... params) {
	  mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
							new AWSIotMqttNewMessageCallback()
	  setCurrentSensorValues(message);
	  return getCurrentSensorValues();
  }

  // AIR Quality messgage
public class AirQualityActivity extends AppCompatActivity {
public void airQuality(View view) {
	RequestQueue requestQueue;
	airURL = "http://www.airnowapi.org/aq/forecast/zipCode/?format=application/json&zipCode="+zipcode+"&date=" + currentDate + "&distance=25&API_KEY=" + APIKEY;
	JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, airURL, (String) null, new Response.Listener<JSONArray>()
	airQualityGauge.setValue(Integer.parseInt(AQI));
}


// Analytics Activity
public class AnalyticsActivity extends AppCompatActivity {
Cartesian cartesian = AnyChart.line();
anyChartView.setChart(cartesian);
}
private class CustomDataEntry extends ValueDataEntry {}

// Gauge library
import pl.pawelkleczkowski.customgauge.CustomGauge;

// Android import classes used
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import java.util.ArrayList;
import java.util.List;






High-level Components on AWS IoT:

Following are high-level components of AWS IoT Platform –

1. Device gateway and MQTT Broker
2. Rules Engine
3. Registry (Things, Shadow and Shadow Service)
4. Security and Identity (Certificates and Policies)

 
Fig : Hardware and Software Component Diagram







Below figure depicts the flow of sensor data from DHT11 to connected mobile devices.
 
TESTING
Following are the unit test cases we considered during the design and development of the project:
1.	Validate if the sensor is correctly connected to the nodeMCU and to the WiFi.
2.	Validate that the MCU is recorded as the publisher for the MQTT topic in AWS IoT Core.
3.	Check the incoming data to the AWS IoT Core and make sure that it is getting updated as per the condition where the sensor is located.
4.	Make sure that the mobile app is registered as the subscriber for the MQTT topic in the AWS IoT Core.
5.	Validate the data received by the mobile app and check with the IoT Core data.
6.	Check the analytics graph generated by the mobile app and validate it with the data in the database.
7.	Validate the Air Quality Index displayed on graph as compared on the Open Weather API.
8.	Check the email notification received after clicking the AWS IoT 1-Click button.
9.	Validate the information received in the email as compared to the current sensor data.
10.	 Validate the data stored in the AWS RDS and DynamoDB and confirm its accuracy. 

INDIVIDUAL CONTRIBUTION

●	MUKESH RANJAN SAHAY
○	Worked on the basic setup of DynamoDB to pass data for Analytics
○	Worked on the Integrating AWS Mobile SDK to the Android Application
○	Worked on the AWS IoT 1-Click button
●	THIRUMALAI NAMBI DOSS PALANI
○	Worked on the configuration of NodeMCU Sensor
○	Worked on sending data from the sensor to AWS IoT Core
○	Created the Lambda function to update values from AWS IoT Core to AWS RDS
●	SUDHA AMARNATH
○	Worked on the configuration of DHT11 Sensor
○	Worked on sending data from IoT Core to IoT Analytics
○	Worked on sending data from the sensor to AWS IoT Core
●	MUTHU KUMAR SUKUMARAN
○	Worked on the Mobile UI
○	Handling data in the Mobile Application
○	Worked on AWS Mobile Hub to pass data from DynamoDB to the Application



SCREENSHOTS

1.	Launcher Activity of the Application
 

2. Sensor Values Displayed on the Screen.
a.	Temperature In C
b.	Fahrenheit in F
c.	Humidity in %
d.	Heat Index in C
e.	Dew point in F
f.	Health related messages displayed depending on the parameters
 


3. Application Paused in the Background
 



4. Restore of the current values when the Main Activity is resumed
 

5. Manually stimulating DHT sensor value changes by testing the sensor by covering it in hand for a few minutes. Values from sensors are changed appropriate messages are displayed on the App screen
 


 


 




6. Navigation Pane options
 

7. Air Quality activity display
 


7. Entering Zipcode for San Jose, CA for air quality and AQI is displayed 
 



8. Entering Zipcode for Phoenix, AZ for air quality and AQI is displayed - showing more degraded air quality
 
9. Moderate AQI in Backerfield, CA.
 



10. Analytics Graph.
 

11. Arduino Software showing NodeMCU board
 



12. Downloading Arduino code to the nodeMCU device
 


13. MQTT connection from NodeMCU to AWS IOT thing and sensor values are sent to AWS IOT shadow table.
 


14. Android App upon starting is connected to AWS IOT and Subscribed to the same AWS IOT topic
 

15. MQTT messages are received from AWS IOT in Json Format at regular intervals
 

16. Verbose output displayed overwriting existing values
 

17. IOT - Thing Configured in AWS with topic as shown in this output
 

18. AWS Shadow table


 



19. MQTT connections establishment from the devices. Usage is shown in AWS
 


20. The number of MQTT messages received and advertised to the IOT devices subscribed to the IOT topic

 

20. Federated Identities for the IOT Thing in AWS Cognito. This is used to access AWS IOT Topic from the Andriod Apps
 







GITHUB SOURCE CODE
 https://github.com/muthu-05/Clim8

REFERENCES
https://learn.adafruit.com/dht/overview
