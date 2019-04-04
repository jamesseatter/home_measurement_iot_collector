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
Type = forking
User = iot
ExecStart = /home/iot/bin/collector_test/collectorservice.sh start
ExecStop = /home/iot/bin/collector_test/collectorservice.sh stop
ExecReload = /home/iot/bin/collector_test/collectorservice.sh reload

[Install]
WantedBy = multi-user.target
```

###Step 2 - startup shell script

  Location  - /home/iot/bin/\<collector or gateway>_\<env>.
  
  ex. `/home/iot/bin/collector_test`
 
  File Name - collectorservice.sh

Content of Collector shell file for Test environment. Modify initial variables as needed for the environment

```
#!/bin/sh
SERVICE_NAME=CollectorServiceTest
CODEENV=test
PATH_TO_JAR=/home/iot/bin/collector_$CODEENV/collectorservice.jar
PATH_TO_STARTUP_LOG=/home/iot/bin/collector_$CODEENV/startup.log
PID_PATH_NAME=/tmp/collectorservice_$CODEENV-pid
SPRING_PROFILE=test

case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            #nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &  echo $! > $PID_PATH_NAME
            nohup java -Dspring.profiles.active=$SPRING_PROFILE -jar $PATH_TO_JAR >> $PATH_TO_STARTUP_LOG 2>&1& echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null & echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac
```

###Step 3 - Security

  sudo chmod +x /home/iot/bin/\<collector or gateway>_\<env>.   
  `sudo chmod +x /home/iot/bin/collector_dev/collectorservice.sh`


###Step 4 - Configure SystemD
`sudo systemctl daemon-reload`

sudo systemctl enable \<collector or gateway>_\<env>

ex. `sudo systemctl enable collector_test`
