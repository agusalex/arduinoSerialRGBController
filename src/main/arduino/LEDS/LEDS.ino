//Serial RGB led controller
//Author: Trevor Shannon
//see http://trevorshp.com/creations/android_led.htm

//pin definitions.  must be PWM-capable pins!
const int redPin = 9;
const int greenPin = 11;
const int bluePin = 10;
const int FADESPEED=10;
const int botonpin=4;
//maximum duty cycle to be used on each led for color balancing.  
//if "white" (R=255, G=255, B=255) doesn't look white, reduce the red, green, or blue max value.
const int max_red = 255;
const int max_green = 255;
const int max_blue = 255;
int Opcion=0;
int Boton=0;
void setup(){
  //set all three of our led pins to output
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
  pinMode(botonpin,INPUT);
  //start the Serial connection
  Serial.begin(9600);
  
}

void loop(){

  BotonState();

  if(Opcion==0){
    animationFade();
    }
 
  if (Serial.available()){
       Opcion=5;
     char led=Serial.read();
     int  power=Serial.parseInt();
     ledOn(led,power);
  }

  
    
  //set the three PWM pins according to the data read from the Serial port
  //we also scale the values with map() so that the R, G, and B brightnesses are balanced.

}


void animationFade(){
int r, g, b;
analogWrite(bluePin, 255);
// fade from blue to violet
for (r = 0; r < 256; r++) {
  if(BotonState()){return;}
  if (Serial.available()){return;}

analogWrite(redPin, r);
delay(FADESPEED);
}
// fade from violet to red
for (b = 255; b > 0; b--) {
  if(BotonState()){return;}
 if (Serial.available()){return;}
analogWrite(bluePin, b);
delay(FADESPEED);
}
// fade from red to yellow
for (g = 0; g < 256; g++) {
  if(BotonState()){return;}
  if (Serial.available()){return;}

analogWrite(greenPin, g);
delay(FADESPEED);
}
// fade from yellow to green
for (r = 255; r > 0; r--) {
 if(BotonState()){return;}
  if (Serial.available()){return;}

analogWrite(redPin, r);
delay(FADESPEED);
}
// fade from green to teal
for (b = 0; b < 256; b++) {
  if(BotonState()){return;}
  if (Serial.available()){return;}

analogWrite(bluePin, b);
delay(FADESPEED);
}
// fade from teal to blue
for (g = 255; g > 0; g--) {
  if(BotonState()){return;}
  if (Serial.available()){return;}

analogWrite(greenPin, g);
delay(FADESPEED);
}
}



boolean BotonState(){
  boolean ret=false;
  Boton=digitalRead(botonpin);
  if(Boton == HIGH){
    Opcion+=1; 
    if(Opcion>=3){
        Opcion=0;}
    ledOn('R',0);
    ledOn('G',0); 
    ledOn('B',0);    
    delay(200);
    ret=true;
    }
    return ret;
  }


void ledOn(char led, int power){

  if (led=='R'){
    
    if(power>max_red){
      analogWrite(redPin,max_red); }
   
    else{
     
      analogWrite(redPin,power);}
   
   
    } 
 
  if(led=='G'){
   
    if(power>max_green){
      analogWrite(greenPin,max_green);
    }
    else{
         analogWrite(greenPin,power);}
   }
 
  if (led=='B'){
        if(power>max_blue){
            analogWrite(bluePin,max_blue);
            }
    
    else{
      analogWrite(bluePin,power);}
    }
    return;
  
  }


