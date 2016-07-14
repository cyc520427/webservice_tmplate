#!/bin/bash

cd `dirname "$0"`;
cd ..

export APP_HOME=`pwd`;

if [ ! -f "$APP_HOME/etc/config.properties" ]; then
	touch $APP_HOME/etc/config.properties
fi;

cat $APP_HOME/etc/config.properties | grep zookeeper.connect= >> /dev/null

if [ "$?" != "0" ]; then
	$APP_HOME/libexec/setup.sh
fi;

$APP_HOME/libexec/daemon.sh stop com.yahoo.ecdata.tiffany.TiffanyServer $@