#!/bin/sh
version=`cat VERSION`
letterFreeVersion=`echo $version | sed -e "s/v//g"`
sed -i "s/{version}/$letterFreeVersion/g" install.nsi