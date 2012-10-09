#include <Usb.h>
#include <AndroidAccessory.h>

AndroidAccessory acc("A3CCTV Project Team",      // Manufacturer
                     "A3CCTV",                  // Model
                     "A3CCTV",                  // Description
                     "1.0",                       // Version
                     "http://kiwook.pe.kr",       // URI
                     "00000000000001");           // Serial

int lightPin = 0;
int ledPin = 9;

int settingTemp = -1;

boolean isConnected = false;

void setup() {
  Serial.begin(9600);
  pinMode(ledPin, OUTPUT);
  acc.powerOn();  
}

void loop() {
  
  isConnected = acc.isConnected();
  
  if (isConnected) {

    int lightLevel = analogRead(lightPin);
    lightLevel = map(lightLevel, 0, 900, 0, 255);
    lightLevel = constrain(lightLevel, 0, 255);
    
    if( settingTemp == -1 ) {

      Serial.println(lightLevel);  
      settingTemp = lightLevel;  

    } else {
      
      byte data[2];
      data[0] = 0x0;
      if(lightLevel > 120){
        digitalWrite(ledPin, HIGH);  
        data[1] = 0x1;
        Serial.println("ON");  
      } else {
        digitalWrite(ledPin, LOW);  
        data[1] = 0x0;
        Serial.println("OFF");
      }
      acc.write(data, 2);
    }
    
    
  } else {
   
    Serial.println("____");  

  }
}
