#!/bin/bash

echo

cd `dirname "$0"`;
cd ..
export APP_HOME=`pwd`;

. "$APP_HOME"/libexec/env.sh

clear
echo "=============================================================="
echo "|              Tiffany - Environment Configuration            |"
echo "=============================================================="
echo
echo

read -p "Enter ZOOKEEPER_CONNECT: " ZOOKEEPER_CONNECT
read -p "Enter JETTY_LOG_DIR: " JETTY_LOG_DIR

DEFAULT_NUM_PARTITION="8";
read -p "Enter NUM_PARTITION [$DEFAULT_NUM_PARTITION]: " NUM_PARTITION
NUM_PARTITION=${NUM_PARTITION:-$DEFAULT_NUM_PARTITION}

DEFAULT_REPLICATION_FACTOR="1";
read -p "Enter REPLICATION_FACTOR [$DEFAULT_REPLICATION_FACTOR]: " REPLICATION_FACTOR
REPLICATION_FACTOR=${REPLICATION_FACTOR:-$DEFAULT_REPLICATION_FACTOR}


echo zookeeper.connect=$ZOOKEEPER_CONNECT > $APP_HOME/etc/config.properties
echo jetty.log.dir=$JETTY_LOG_DIR  >> $APP_HOME/etc/config.properties
echo num.partitions=$NUM_PARTITION >> $APP_HOME/etc/config.properties
echo replication.factor=$REPLICATION_FACTOR >> $APP_HOME/etc/config.properties

echo
echo
