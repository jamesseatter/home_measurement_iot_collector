# home_heating_iot_device

[![CircleCI](https://circleci.com/gh/jamesseatter/home_heating_iot_collector.svg?style=svg)](https://circleci.com/gh/jamesseatter/home_heating_iot_collector)

Home Heating IOT Device

The project is focused on building a system to monitor the temperature of the water entering the house from a shared common heating source as its base and will be extended to other measurements over time. That hot water is then distributed to taps and the underfloor heating system. The project will collect temperatures from strategic points to monitor water temperature and record the data for viewing within a web based front end.

The driver for this project is to monitor the hot water for system faults and provide an alarm when there are issues that need intervention. There is a history of system failures over the years that have taken hours and days to be corrected due to various factors.


This code covers the end point that measures the temperature and sends it to the Edge.

**Code Goals**
   * Collect data from 1 or more sensors
   * Send data to an Edge controller
   * Use authentication and roles
   * Update device configuration remotely via Edge
   * Store data localy if Edge is not accessible and replay

**Code/hardware Components**
   * Raspberry Pi Zero W with a DS18B20 temperature probe.
   * Java 8 code base
