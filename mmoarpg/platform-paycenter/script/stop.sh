#!/bin/sh
PID=0
APPNAME=platform-paycenter

javaps=`jps -l | grep $APPNAME`
if [ -n "$javaps" ]; then
 PID=`echo $javaps | awk '{print $1}'`
else
 PID=0
fi

if [ $PID -eq 0 ]; then
 echo -e "$APPNAME is not running."
else
 echo -n -e "$APPNAME stop " && echo -n -e "\033[s...";
 kill $PID
 until [ `ps --pid $PID | grep -c $PID` = '0' ]
 do
  echo -n -e ".";
  sleep 1;
 done
 echo -e " [  ok  ]"
fi