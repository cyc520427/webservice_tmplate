#!/bin/bash

export APP_HOME=${APP_HOME:="`dirname $0`/.."}

. "$APP_HOME"/libexec/env.sh

# get arguments
command=$1
shift

${JAVA_HOME}/bin/java -classpath $CLASSPATH $JVM_OPTS $command "$@"
