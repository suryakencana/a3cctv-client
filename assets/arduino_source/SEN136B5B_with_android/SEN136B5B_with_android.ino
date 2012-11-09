#include <Usb.h>
#include <AndroidAccessory.h>

AndroidAccessory acc("A3CCTV Project Team",
"A3CCTV",                  
"A3CCTV",                  
"1.0",                      
"https://play.google.com/store/apps/details?id=kr.a3cctv.client",
"00000000000001");           


const int STATUS_READY  = 0;
const int STATUS_SHOT   = 1;
const int STATUS_WARMUP = 2;

const int rangePin = 7;
const int redPin = 11;

static int baseLine = -1;
static int baseLineTemp = 0;
static int baseLineCount = 0;

boolean isConnected = false;

void setup() {

  Serial.begin(9600);
  acc.powerOn();  

}


void loop()
{

  isConnected = acc.isConnected();

  if ( isConnected ) {

    long distance;
    byte data[1];

    data[0] = STATUS_READY;

    pinMode(rangePin, OUTPUT);
    digitalWrite(rangePin, LOW);
    delayMicroseconds(2);
    digitalWrite(rangePin, HIGH);
    delayMicroseconds(5);
    digitalWrite(rangePin, LOW);

    pinMode(rangePin, INPUT);
    distance = microsecondsToCentimeters(pulseIn(rangePin, HIGH));

    if ( baseLine == -1 ) {  
      baseLineTemp = (baseLineTemp + distance) / 2;
      if ( baseLineCount < 60 ) {
        baseLineCount++;
      } 
      else {
        baseLine = baseLineTemp;
      }

      data[0] = STATUS_WARMUP;

      Serial.print(baseLineCount);
      Serial.print(" ");
      Serial.println(baseLineTemp);

    } 
    else {
      digitalWrite(redPin, HIGH);
      if ( distance < baseLine ) {
        Serial.println("shot");
        data[0] = STATUS_SHOT;
      }
    }

    acc.write(data, 1);

    if ( data[0] == 1 ) {
      delay(3000);   
    } 
    else {
      delay(200); 
    }   

  }

}

long microsecondsToCentimeters(long microseconds)
{
  return microseconds / 29 / 2;
}


