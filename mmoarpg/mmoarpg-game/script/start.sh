#!/bin/sh
echo "start game server ..."
java -server -Xms4G -Xmx4G -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+HeapDumpOnOutOfMemoryError -Xloggc:gc.log -javaagent:agent.jar -jar $PWD/mmoarpg-game.jar&