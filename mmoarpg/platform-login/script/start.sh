#!/bin/sh
echo "start login server ..."
java -server -Xms2G -Xmx2G -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+HeapDumpOnOutOfMemoryError -Xloggc:gc.log -jar platform-login.jar&