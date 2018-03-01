#!/bin/sh
version=`cat ./resources/VERSION`
letterFreeVersion=`echo $version | sed -e "s/v//g"`
sed -i "s/{version}/$letterFreeVersion/g" install.nsi