
int lightPin = 0;
int ledPin = 9;

void setup() {
  pinMode(ledPin, OUTPUT);
}

void loop() {
 
    int lightLevel = analogRead(lightPin);
    lightLevel = map(lightLevel, 0, 900, 0, 255);
    lightLevel = constrain(lightLevel, 0, 255);
   
   
    if (lightLevel > 120) {
        digitalWrite(ledPin, HIGH); 
    } else {
        digitalWrite(ledPin, LOW);
    } 
    
    
  
}
