FILE=collector_test.service
if test -f "$FILE"; then
    echo "Moving $FILE to /lib/systemd/system"
    sudo mv collector_test.service /lib/systemd/system/collector_test.service
    echo "SystemD daemon reload"
    sudo systemctl daemon-reload
fi

sudo systemctl restart collector_test.service
sudo systemctl status collector_test.service
