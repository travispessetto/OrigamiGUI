#!/bin/sh
version=`cat ./src/main/resources/VERSION`
letterFreeVersion=`echo $version | sed -e "s/v//g"`
sed -i "s/{version}/$letterFreeVersion/g" launch4j.cfg.xml
sed -i "s/{version}/$letterFreeVersion/g" install.nsi
