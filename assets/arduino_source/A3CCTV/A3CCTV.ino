#include <Usb.h>
#include <AndroidAccessory.h>

AndroidAccessory acc("A3CCTV Project Team",      // Manufacturer
                     "A3CCTV",                  // Model
                     "A3CCTV",                  // Description
                     "1.0",                       // Version
                     "https://play.google.com/store/apps/details?id=kr.a3cctv.client",       // URI
                     "00000000000001");           // Serial

int lightPin = 0;
int ledPin = 9;

int settingTemp = -1;

boolean isConnected = false;
boolean isInit = false;

void setup() {
//  Serial.begin(9600);
  pinMode(ledPin, OUTPUT);
  acc.powerOn();  
}

void loop() {
  
  isConnected = acc.isConnected();
  
  if (isConnected) {

    int lightLevel = analogRead(lightPin);
    lightLevel = map(lightLevel, 0, 900, 0, 255);
    lightLevel = constrain(lightLevel, 0, 255);
    
//    if( settingTemp == -1 ) {
//
//      Serial.println(li/ghtLevel);  
//      settingTemp = lightLevel;  
//
//    } else {
      
      byte data[1];

      if(lightLevel > 60){
        digitalWrite(ledPin, HIGH);  
        data[0] = 1;
//        Serial.println("ON");  
      } else {
        digitalWrite(ledPin, LOW);  
        data[0] = 0;
//        Serial.println("OFF");
      }
      
//      data[0] = 9;

      if (isInit) {
         acc.write(data, 1);
      } else {
         delay(5000);
          acc.write(data, 1);
         isInit = true;
      }
//    }
    
    
  } else {
   
//    Serial.println("____");  

  }
 
 delay(1000);
}
