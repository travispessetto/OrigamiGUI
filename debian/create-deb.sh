#!/bin/sh
cd ./debian
version=`cat ../src/main/resources/VERSION`
letterFreeVersion=`echo $version | sed -e "s/v//g"`
mkdir origami-smtp_$version
mkdir origami-smtp_$version/DEBIAN
mkdir -p origami-smtp_$version/etc/menu
mkdir -p origami-smtp_$version/usr/bin
mkdir -p origami-smtp_$version/etc/ssl/certs
mkdir -p origami-smtp_$version/usr/share/applications
cp ../target/OrigamiGUI*.jar ./origami-smtp.jar
./jar2sh.sh linux-launch.config
cp origami-smtp.jar origami-smtp_$version/usr/bin/origami-smtp.jar
cp origami-smtp.sh origami-smtp_$version/usr/bin/origami-smtp.sh
cp ../license.txt origami-smtp_$version/usr/bin/license.txt
cp ../VERSION origami-smtp_$version/usr/bin/VERSION
sed -i "s/{version}/$letterFreeVersion/g" debian-control
cp debian-control origami-smtp_$version/DEBIAN/control
cp origami-smtp.desktop origami-smtp_$version/usr/share/applications/origami-smtp.desktop
sed -i "s/{version}/$letterFreeVersion/g" ./origami-smtp_$version/usr/share/applications/origami-smtp.desktop
cat ../Origami_CA.crt > origami-smtp_$version/etc/ssl/certs/Origami_CA.pem
dpkg-deb --build origami-smtp_$version
rm -rf origami-smtp_$version
mv origami-smtp_$version.deb ../
cd ..
if test -e "origami-smtp_$version.deb"; then
	echo "origami-smtp_$version.deb created"
    return 0
else
    echo "origami-smtp_$version.deb creation failed"
    return 1
fi
