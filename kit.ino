//piedinatura umidità/temperaura: segnale,vcc,gnd
#include <DHT.h>
#include "DHT.h"

#define DHTTYPE DHT11
#define DHTPIN 8//il pin 8 è quello a cui collego il sensore di temp/umindità(segnale)
DHT dht(DHTPIN, DHTTYPE);//dht oggetto di DHT con 2 parametri: DHTPIN,DHTTYPE

#define sogliaTU 28
int led1 = 9;
int led2 = 10;
int led3 = 11;


#define sogliaF 50
int fotoresistenza = A0; //porta in cui si legge il valore(analog)
int luminosita;//valore letto dalla fotoresistenza

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);
  pinMode(fotoresistenza, INPUT);
  pinMode(DHTPIN, INPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  
  int h = dht.readHumidity();
  int t = dht.readTemperature();
  Serial.println("Temperatura: " + String(t) +"°");
  
  if (t >= sogliaTU) {
    Serial.println("Led1 ACCESO");
    Serial.println("Led2 SPENTO");
    digitalWrite(led1, HIGH);
    digitalWrite(led2, LOW);
  }
  else {
    Serial.println("Led1 SPENTO");
    Serial.println("Led2 ACCESO");
    digitalWrite(led2, HIGH);
    digitalWrite(led1, LOW);
  }


  luminosita = analogRead(fotoresistenza); //legge il pin fotoresistenza
  luminosita = luminosita / 4;
  Serial.println("Luminosità: " +String(luminosita));
  
  if (luminosita < sogliaF) {
    digitalWrite(led3, HIGH); //se è buio accendo il led
    Serial.println("Led3 ACCESO");
  }
  else {
    digitalWrite(led3, LOW); //se c'è luce lo spengo
    Serial.println("Led3 SPENTO");
  }

  Serial.println(" ");
  delay(1000);
}
