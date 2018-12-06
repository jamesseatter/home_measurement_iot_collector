# home_heating_iot_client
Home Heating IOT Client written in Java

The project is focused on building a system to monitor the heat of the water entering the house that provides the hot water for taps and the heating system
The hot water is provided by a central system for a number of houses and it fails to often. Whats more the system has no monitoring so everyone finds out when the shower in the morning :(

This cocde covers the end point that measures the temperature and sends it to the Edge.

Project Components

End Point (Client)
  Raspberry Pi Zero W with a DS18B20 temperature probe.
  
Edge Device
  Raspberry Pi 3 B+.
  
Storage
  NAS based MySQL DB
  
Web
  One of
    Angular 6
    Java Thymeleaf
