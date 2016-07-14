#!/bin/bash

cd `dirname "$0"`;

#################################################################################
# Clean up
#################################################################################

#mv  target tmp_target
rm -Rf output


#################################################################################
# Install 3rd-party dependencies
#################################################################################

#if [ ! -f ~/.m2/repository/oracle/oracle-jdbc/6/oracle-jdbc-6.jar ]; then
#mvn install:install-file -Dfile=./lib/ojdbc6.jar -DgroupId=oracle -DartifactId=oracle-jdbc -Dversion=6 -Dpackaging=jar
#fi;

#################################################################################
# Build Project
#################################################################################

mvn package

#################################################################################
# Prepare Output
#################################################################################

mkdir output
mkdir output/runtime
mkdir output/runtime/logs

cp -Rf bin output/runtime/bin
cp -Rf libexec output/runtime/libexec
cp -Rf etc output/runtime/etc
cp -Rf healthcheck output/runtime/healthcheck

chmod 755 output/runtime/bin/*
chmod 755 output/runtime/libexec/*

#################################################################################
# Prepare Libs
#################################################################################

mvn -DoutputDirectory=output/runtime/lib dependency:copy-dependencies -DincludeScope=runtime
mv target/webservice* output/runtime

#################################################################################
# Tar
#################################################################################

cd output
mv runtime webservice
tar -zcf webservice.tar.gz webservice
mv webservice runtime
cd ..

#################################################################################
# Clean Temporary Files
#################################################################################

#rm dependency-reduced-pom.xml
#rm -Rf target
#mv tmp_target target
