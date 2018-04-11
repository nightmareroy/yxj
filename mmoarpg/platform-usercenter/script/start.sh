#!/bin/sh
echo "start usercenter ..."
java -server -Xms1G -Xmx1G -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+HeapDumpOnOutOfMemoryError -Xloggc:gc.log -jar platform-usercenter.jar&