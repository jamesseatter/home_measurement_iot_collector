#!/bin/sh
SERVICE_NAME=CollectorServiceDev
CODEENV=dev
PATH_TO_JAR=/home/iot/bin/collector_${CODEENV}/collectorservice.jar
PATH_TO_STARTUP_LOG=/home/iot/bin/collector_${CODEENV}/startup.log
PID_PATH_NAME=/tmp/collectorservice_${CODEENV}-pid

case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f ${PID_PATH_NAME} ]; then
            #nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &  echo $! > $PID_PATH_NAME
            nohup java -jar ${PATH_TO_JAR} >> ${PATH_TO_STARTUP_LOG} 2>&1& echo $! > ${PID_PATH_NAME}
            echo "$SERVICE_NAME starting ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f ${PID_PATH_NAME} ]; then
            PID=$(cat ${PID_PATH_NAME});
            echo "$SERVICE_NAME stoping ..."
            kill ${PID};
            echo "$SERVICE_NAME stopped ..."
            rm ${PID_PATH_NAME}
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f ${PID_PATH_NAME} ]; then
            PID=$(cat ${PID_PATH_NAME});
            echo "$SERVICE_NAME stopping ...";
            kill ${PID};
            echo "$SERVICE_NAME stopped ...";
            rm ${PID_PATH_NAME}
            echo "$SERVICE_NAME starting ..."
            nohup java -jar ${PATH_TO_JAR} /tmp 2>> /dev/null >> /dev/null & echo $! > ${PID_PATH_NAME}
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac
