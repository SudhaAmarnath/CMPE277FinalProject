


CMPE 277 SMARTPHONE APPLICATION DEVELOPMENT

## Environmental Monitoring System Using IoT and Cloud Service at Real-Time
		 	 	 	
In this project, we have designed a Cloud - IoT based environmental monitoring system mobile app. DHT11 sensors are used to get the current temperature and humidity values. These input values from the DHT11 sensor are interpreted by the NodeMCU- ESP2866-12E module. Arduino C code is downloaded to the NodeMCU to convert the analog input values from the DHT11 sensor to the JSON format values. Along with the default temperature and humidity values, heat index, dewpoint, temperature in Fahrenheit is also calculated. These JSON values are then passed on Wifi to AWS IOT core endpoint subscribing to a user-defined MQTT topic where the clients using this mobile app can gain access to the same MQTT topic by an unauthorized AWS Cognito ID. An android app is designed which acts as an MQTT subscriber/client and it interprets the values received from the AWS MQTT publisher. The sensor values are displayed on the dashboard of the app with information showing health-related messages based on the received values. Air Quality is checked in the app by providing area zip code by the user to find out whether the air quality in that particular area is safe.

Below figure depicts the flow of sensor data from DHT11 to connected mobile devices.

![image](https://user-images.githubusercontent.com/42689991/97121435-71edf080-16db-11eb-9032-9fa7aed4270b.png)


## Technolgy Stack

### Hardware

The below diagram depicts a picture of the NodeMCU microcontroller used.

- NodeMCU - ESP826


![image](https://user-images.githubusercontent.com/42689991/97120318-744c4c80-16d3-11eb-8d81-e343555c6a8a.png)


- DHT11 - Temperature and Humidity Sensor


![image](https://user-images.githubusercontent.com/42689991/97120343-92b24800-16d3-11eb-9e86-6a3b2464a76a.png)



#### Significance of using DHT11 Sensor

- 3 to 5V power and I/O
- 5mA max current use during conversion (while requesting data)
- Good for 20-80% humidity readings with 5% accuracy
- Good for 0-50°C temperature readings ±2°C accuracy
- No more than 1 Hz sampling rate (once every second)
- Body size 15.5mm x 12mm x 5.5mm
- 4 pins with 0.1" spacing


### Software

- Arduino
- Android Studio


### Cloud Services

- AWS Mobile SDK
- AWS IoT Core
- AWS IoT Topic
- AWS Cognito
- AWS IoT 1-Click
- AWS SNS
- AWS SES
- AWS IoT Analytics
- AWS Lambda
- AWS DynamoDB
- AWS RDS
- AWS Mobile Hub	



## Screens

1. Launcher Activity of the Application


![image](https://user-images.githubusercontent.com/42689991/97120132-56321c80-16d2-11eb-8f03-4d1490eeba11.png)

 

2. Sensor Values Displayed on the Screen.
- Temperature In C
- Fahrenheit in F
- Humidity in %
- Heat Index in C
- Dew point in F
- Health related messages displayed depending on the parameters

![image](https://user-images.githubusercontent.com/42689991/97120887-aeb7e880-16d7-11eb-9d37-9b89a28e96c7.png)


3. Application Paused in the Background
 
 ![image](https://user-images.githubusercontent.com/42689991/97120890-b4adc980-16d7-11eb-8f94-753d37bdc992.png)


4. Restore of the current values when the Main Activity is resumed

![image](https://user-images.githubusercontent.com/42689991/97120893-b8415080-16d7-11eb-957a-175ab01a99b9.png)

 
5. Manually stimulating DHT sensor value changes by testing the sensor by covering it in hand for a few minutes. Values from sensors are changed appropriate messages are displayed on the App screen

![image](https://user-images.githubusercontent.com/42689991/97120895-bc6d6e00-16d7-11eb-9853-f68a7defc096.png)

 
6. Entering Zipcode for San Jose, CA for air quality and AQI is displayed 
 
![image](https://user-images.githubusercontent.com/42689991/97121066-e83d2380-16d8-11eb-9e05-d59005e66c93.png)


7. Entering Zipcode for Phoenix, AZ for air quality and AQI is displayed - showing more degraded air quality

![image](https://user-images.githubusercontent.com/42689991/97121071-ef643180-16d8-11eb-80c1-039056936202.png)

 
8. Moderate AQI in Backerfield, CA.

![image](https://user-images.githubusercontent.com/42689991/97121084-f8550300-16d8-11eb-956e-3cd05976465b.png)


9. Arduino Software showing NodeMCU board

![image](https://user-images.githubusercontent.com/42689991/97121303-7665d980-16da-11eb-8be3-9df2dc21b6e9.png)

 
10. MQTT connection from NodeMCU to AWS IOT thing and sensor values are sent to AWS IOT shadow table.

![image](https://user-images.githubusercontent.com/42689991/97121336-ba58de80-16da-11eb-820a-9d14e37f183a.png)


11. MQTT messages are received from AWS IOT in Json Format at regular intervals

![image](https://user-images.githubusercontent.com/42689991/97121353-cfce0880-16da-11eb-9c23-62a25dd764e6.png)

 
12. AWS Shadow table

![image](https://user-images.githubusercontent.com/42689991/97121695-8501c000-16dd-11eb-896a-5d35bf3ee6d6.png)

 
13. The number of MQTT messages received and advertised to the IOT devices subscribed to the IOT topic

![image](https://user-images.githubusercontent.com/42689991/97121706-8fbc5500-16dd-11eb-9a32-5f0bf568f80d.png)

 
14. Federated Identities for the IOT Thing in AWS Cognito. This is used to access AWS IOT Topic from the Andriod Apps

![image](https://user-images.githubusercontent.com/42689991/97121710-95199f80-16dd-11eb-9439-8fc25225ca75.png)





