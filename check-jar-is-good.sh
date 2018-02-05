#!/bin/sh
cp origami.jar origami-test.zip
unzip -o origami-test.zip -d origami-test
cd origami-test
success=0
if test -e "VERSION"; then
	echo "VERSION found"
else
    echo "ERROR: VERSION NOT FOUND IN JAR!"
    success=1
fi
if test -e "license.txt"; then
	echo "license.txt found"
else
    echo "license.txt NOT FOUND IN JAR!"
    success=1
fi
cd ..
exit $success
