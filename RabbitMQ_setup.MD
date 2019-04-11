# Rabbit MQ setup #



##Add Users##
Replace the password XXXXXXXX with a new secure password.
```
sudo rabbitmqctl add_user homemonitor XXXXXXXX
sudo rabbitmqctl add_user homemonitor_test XXXXXXXX
sudo rabbitmqctl add_user homemonitor_dev XXXXXXXX
```
<!--sudo rabbitmqctl set_permissions -p / testuser ".*" ".*" ".*"-->


##Add VHosts##
```
sudo rabbitmqctl add_vhost home_monitor
sudo rabbitmqctl set_permissions -p home_monitor admin ".*" ".*" ".*"
sudo rabbitmqctl set_permissions -p home_monitor homemonitor ".*" ".*" ".*"
```

```
sudo rabbitmqctl add_vhost home_monitor_test
sudo rabbitmqctl set_permissions -p home_monitor_test admin ".*" ".*" ".*"
sudo rabbitmqctl set_permissions -p home_monitor_test homemonitor_test ".*" ".*" ".*"
```

```
sudo rabbitmqctl add_vhost home_monitor_dev
sudo rabbitmqctl set_permissions -p home_monitor_dev admin ".*" ".*" ".*"
sudo rabbitmqctl set_permissions -p home_monitor_dev homemonitor_dev ".*" ".*" ".*"
```

##Add Exchanges##
Replace the password XXXXXXXX with the admin password.
```
sudo rabbitmqadmin declare exchange --vhost=home_monitor name=E_home_monitor type=topic -u admin -p XXXXXXXX
sudo rabbitmqadmin declare exchange --vhost=home_monitor_test name=E_home_monitor_test type=topic -u admin -p XXXXXXXX
sudo rabbitmqadmin declare exchange --vhost=home_monitor_dev name=E_home_monitor_dev type=topic -u admin -p XXXXXXXX
```

##Add Queues##
Replace the password XXXXXXXX with the admin password.
```
sudo rabbitmqadmin declare queue --vhost=home_monitor name=Q_home_monitor durable=true -u admin -p XXXXXXXX
sudo rabbitmqadmin declare queue --vhost=home_monitor_test name=Q_home_monitor_test durable=true -u admin -p XXXXXXXX
sudo rabbitmqadmin declare queue --vhost=home_monitor_dev name=Q_home_monitor_dev durable=true -u admin -p XXXXXXXX
```

##Add Bindings##
Replace the password XXXXXXXX with the admin password.
```
sudo rabbitmqadmin --vhost=home_monitor declare binding source=E_home_monitor destination_type="queue" destination=Q_home_monitor routing_key="measurement.*" -u admin -p XXXXXXXX
sudo rabbitmqadmin --vhost=home_monitor declare binding source=E_home_monitor destination_type="queue" destination=Q_home_monitor routing_key="registration.*" -u admin -p XXXXXXXX

sudo rabbitmqadmin --vhost=home_monitor_test declare binding source=E_home_monitor_test destination_type="queue" destination=Q_home_monitor_test routing_key="measurement.*" -u admin -p XXXXXXXX
sudo rabbitmqadmin --vhost=home_monitor_test declare binding source=E_home_monitor_test destination_type="queue" destination=Q_home_monitor_test routing_key="registration" -u admin -p XXXXXXXX

sudo rabbitmqadmin --vhost=home_monitor_dev declare binding source=E_home_monitor_dev destination_type="queue" destination=Q_home_monitor_dev routing_key="measurement.*" -u admin -p XXXXXXXX
sudo rabbitmqadmin --vhost=home_monitor_dev declare binding source=E_home_monitor_dev destination_type="queue" destination=Q_home_monitor_dev routing_key="registration" -u admin -p XXXXXXXX
```

