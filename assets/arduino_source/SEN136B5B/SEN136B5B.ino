


const int pingPin = 7;
const int redPin = 11;

static int baseLine = -1;
static int baseLineTemp = 0;
static int baseLineCount = 0;
 
void setup() {
  Serial.begin(9600);
}
 
void loop()
{
  long distance;
 
  pinMode(pingPin, OUTPUT);
  digitalWrite(pingPin, LOW);
  delayMicroseconds(2);
  digitalWrite(pingPin, HIGH);
  delayMicroseconds(5);
  digitalWrite(pingPin, LOW);

  pinMode(pingPin, INPUT);
  distance = microsecondsToCentimeters(pulseIn(pingPin, HIGH));
  
  if ( baseLine == -1 ) {  
    baseLineTemp = (baseLineTemp + distance) / 2;
    if ( baseLineCount < 60 ) {
      baseLineCount++;
    } else {
      baseLine = baseLineTemp;
    }
    
    Serial.print(baseLineCount);
    Serial.print(" ");
    Serial.println(baseLineTemp);
    
  } else {
    digitalWrite(redPin, HIGH);
    
  }
 
//  Serial.print(cm);
 
  delay(100);
}
 
long microsecondsToCentimeters(long microseconds)
{
  // The speed of sound is 340 m/s or 29 microseconds per centimeter.
  // The ping travels out and back, so to find the distance of the
  // object we take half of the distance travelled.
  return microseconds / 29 / 2;
}
