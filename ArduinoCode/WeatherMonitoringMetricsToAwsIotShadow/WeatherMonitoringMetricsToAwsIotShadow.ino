#include <Arduino.h>
#include <Stream.h>
#include <math.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

//AWS
#include "sha256.h"
#include "Utils.h"


//WEBSockets
#include <Hash.h>
#include <WebSocketsClient.h>

//MQTT PUBSUBCLIENT LIB 
#include <PubSubClient.h>

//AWS MQTT Websocket
#include "Client.h"
#include "AWSWebSocketClient.h"
#include "CircularByteBuffer.h"

//DHT
#include "DHT.h"

#define DHTTYPE DHT11   // DHT 11

// Heat Index Constants
#define c1 (-42.379)
#define c2 (2.04901523)
#define c3 (10.14333127)
#define c4 (-0.22475541)
#define c5 (-0.00683783)
#define c6 (-0.05481717)
#define c7 (0.00122874)
#define c8 (0.00085282)
#define c9 (-0.00000199)


extern "C" {
  #include "user_interface.h"
}


//AWS IOT config, change these:
char wifi_ssid[]       = "enter-ssid";
char wifi_password[]   = "enter-password";
char aws_endpoint[]    = "enter-endpoint.iot.****.com";
char aws_key[]         = "enter-iam-key";
char aws_secret[]      = "enter-iam-secret-key";
char aws_region[]      = "eu-****-1";
const char* aws_topic  = "$aws/things/shadow-device/shadow/update";
int port = 443;


//MQTT config
const int maxMQTTpackageSize = 512;
const int maxMQTTMessageHandlers = 1;


// DHT Sensor
uint8_t DHTPin = D3; 
               
// Initialize DHT sensor.
DHT dht(DHTPin, DHTTYPE);                

float Temperature;
float Kelvin;
float Fahrenheit;
float HeatIndex; 
float HeatIndexCelsius;
float Humidity;
float f;
float h;

const int len = 200;  
char buf[len];
String str = "";
int rc;

ESP8266WiFiMulti WiFiMulti;

AWSWebSocketClient awsWSclient(1000);

PubSubClient client(awsWSclient);

//# of connections
long connection = 0;

//generate random mqtt clientID
char* generateClientID () {
  char* cID = new char[23]();
  for (int i=0; i<22; i+=1)
    cID[i]=(char)random(1, 256);
  return cID;
}

//count messages arrived
int arrivedcount = 0;

//handle mqtt messages
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

//mqtt connection
bool connect () {

    if (client.connected()) {    
        client.disconnect ();
    }  
    delay (1000);
    Serial.print (millis ());
    Serial.print (" - conn: ");
    Serial.print (++connection);
    Serial.print (" - (");
    Serial.print (ESP.getFreeHeap ());
    Serial.println (")");


    //creating random client id
    char* clientID = generateClientID ();
    
    client.setServer(aws_endpoint, port);
    if (client.connect(clientID)) {
      Serial.println("connected");     
      return true;
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      return false;
    }
    
}

//susbcribe to AWS mqqt IoT
void subscribe () {
    client.setCallback(callback);
    client.subscribe(aws_topic);
   //subscript to a topic
    Serial.println("Subscribed to MQTT topic");
}

//send a message to AWS mqqt IoT
void sendmessage () {

    // Get temperature values from DHT11 - Celcius
    Temperature = dht.readTemperature();
    
    // Get Humidity values from DHT11
    Humidity = dht.readHumidity();

    // Calcualte Fahrenheit
    Fahrenheit = Temperature * 1.8 + 32;

    // Calculate Kelvin
    Kelvin = Temperature + 273;

    // Calculate Heat Index
    f = Fahrenheit;
    h = Humidity;
    HeatIndex = c1+c2*(f)+c3*(h)+c4*(f)*(h)+c5*(pow(f,2))+c6*(pow(h,2))+c7*(pow(f, 2))*(h)+c8*(f)*(pow(h, 2))+c9*(pow(f, 2))*(pow(h, 2)); 
    HeatIndexCelsius = ((((HeatIndex)-32)*5)/9);

    // Send Temperature value to AWS IoT
    //delay (100);
    
    str = AwsShadowJsonFormat(Temperature, Fahrenheit, Humidity, HeatIndex);
    str.toCharArray(buf,len);
    Serial.println(buf);
    rc = client.publish(aws_topic, buf);

    Serial.println ("----------------------------------------------------------");
    
}

void setup() {
  
    wifi_set_sleep_type(NONE_SLEEP_T);
    Serial.begin (115200);
    delay (2000);
    Serial.setDebugOutput(1);

    // initialize dht11 to read data on the pin D3
    pinMode(DHTPin, INPUT);  
    dht.begin();              

    // connect to WiFi
    WiFiMulti.addAP(wifi_ssid, wifi_password);
    Serial.println ("connecting to wifi");
    while(WiFiMulti.run() != WL_CONNECTED) {
        delay(100);
        Serial.print (".");
    }
    Serial.println ("\nconnected");

    // set AWS parameters    
    awsWSclient.setAWSRegion(aws_region);
    awsWSclient.setAWSDomain(aws_endpoint);
    awsWSclient.setAWSKeyID(aws_key);
    awsWSclient.setAWSSecretKey(aws_secret);
    awsWSclient.setUseSSL(true);

    if (connect ()){
      subscribe ();
    }

}

void loop() {
  
  // when connected, send data in a loop
  if (awsWSclient.connected ()) {    
      client.loop ();
      sendmessage();
      // wait for 10 secs after resending the data
      Serial.println ("Wait 10 seconds");      
      delay (10000);
  } else {
    // reconnect to the AWS Server
    if (connect ()){
      subscribe ();      
    }
  }

}

// make json format for sending the data
String AwsShadowJsonFormat(float tempValue, float fahrenheitValue, float humidityValue, float heatIndexValue) {

  // AWS shadow JSON format optimized variable names
  //str = "{\"state\":{\"reported\":{\"fah\":79.30,\"tem\":26.30,\"hum\":48.00,\"hid\":80.10}}}"
  
  String str = "";
  
  str += "{";
  str += "\"state\":";
  str +=   "{\"reported\":";
  str +=     String("{\"fah\":") + roundf(100 * fahrenheitValue)/100;
  str +=     String(",\"tem\":") + roundf(100 * tempValue)/100; 
  str +=     String(",\"hum\":") + roundf(100 * humidityValue)/100; 
  str +=     String(",\"hid\":") + roundf(100 * heatIndexValue)/100 + "}"; 
  str +=   "}";
  str += "}";

  return str;
  
}
