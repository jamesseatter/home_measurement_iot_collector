# Setting up a new IOT Device using Raspbian

##Change the hostname of the system

##Enable SSH

##Add IOT User
[https://www.digitalocean.com/community/tutorials/how-to-create-a-sudo-user-on-ubuntu-quickstart]

`adduser iot <password>`


##Give user SUDO Access
[https://www.digitalocean.com/community/tutorials/how-to-create-a-sudo-user-on-ubuntu-quickstart]

`usermod -aG sudo <username>`


##Create IOT folder structure
under /home/\<username> create the following
```
mkdir bin/<collector or gateway>_test
mkdir bin/<collector or gateway>_prod
```


##Configure SystemD
###Step 1 - Unit configuration file
  Location -  /lib/systemd/system
  
  File Name - \<collector or gateway>_\<env>.service.
  
  ex. `collector_dev.service`

Content of Collector File for Test environment. Modify as needed.

Use sudo nano /lib/systemd/system/\<collector or gateway>_\<env>.service
```
[Unit]
Description = Home measurement collector service
After = syslog.target

[Service]
WorkingDirectory=/home/iot/bin/collector_test
Type = simple
User = iot
ExecStart = /usr/bin/java -jar -Dspring.profiles.active=test collector.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy = multi-user.target
```

###Step 2 - Security

  sudo chmod +x /home/iot/bin/\<collector or gateway>_\<env>.   
  `sudo chmod +x /home/iot/bin/collector_dev/collectorservice.sh`


###Step 3 - Configure SystemD
`sudo systemctl daemon-reload`

sudo systemctl enable \<collector or gateway>_\<env>

ex. `sudo systemctl enable collector_test`
