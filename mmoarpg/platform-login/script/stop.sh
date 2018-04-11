#!/bin/sh
PID=0
DIR=`dirname $0`
PIDFILE=$DIR/game.pid
if [ -f $PIDFILE ]; then
 PID=`cat $PIDFILE`
fi

APPNAME=login-server
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