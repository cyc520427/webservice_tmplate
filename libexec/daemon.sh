#!/bin/bash

usage="Usage: daemon.sh (start|stop) <command> <args...>"

# if no args specified, show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi

export APP_HOME=${APP_HOME:="`dirname $0`/.."}
. "$APP_HOME"/libexec/env.sh

# get arguments
startStop=$1
shift
command=$1
shift

pid=$DIR_PID/$USER-$command-$@.pid
#log=$DIR_LOG/$USER-$command-$@-$HOSTNAME.out

case $startStop in

  (start)

    mkdir -p "$DIR_PID"
    mkdir -p "$DIR_LOG"

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo $command running as process `cat $pid`.  Stop it first.
        exit 1
      fi
    fi

    echo starting $command #, logging to $log
    nohup ${JAVA_HOME}/bin/java -classpath $CLASSPATH $JVM_OPTS $command "$@" > /dev/null & #> "$log" 2>&1 < /dev/null &
    echo $! > $pid
    sleep 1; #head "$log"
    ;;
          
  (stop)

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo stopping $command
        kill `cat $pid`
      else
        echo no $command to stop
      fi
    else
      echo no $command to stop
    fi
    ;;

  (*)
    echo $usage
    exit 1
    ;;

esac
