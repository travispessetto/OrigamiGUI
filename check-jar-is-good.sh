#!/bin/sh
cp Origami\ SMTP.jar origami-test.zip
unzip origami-test.zip -d origami-test
cd origami-test
success=0
ls
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
return $success