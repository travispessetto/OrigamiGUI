#!/bin/sh
version=`cat VERSION`
sed -i "s/{version}/$version/g" .travis.yml