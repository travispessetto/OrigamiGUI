#!/bin/sh
cd ./debian
version=`cat ../VERSION`
letterFreeVersion=`echo $version | sed -e "s/v//g"`
mkdir origami-smtp_$version
mkdir origami-smtp_$version/DEBIAN
mkdir -p origami-smtp_$version/etc/menu
mkdir -p origami-smtp_$version/usr/bin
mkdir -p origami-smtp_$version/etc/ssl/certs
mkdir -p origami-smtp_$version/usr/share/applications
cp ../Origami\ SMTP.jar ./Origami.SMTP.jar
./jar2sh.sh linux-launch.config
cp Origami.SMTP.jar origami-smtp_$version/usr/bin/Origami.SMTP.jar
cp Origami.SMTP.sh origami-smtp_$version/usr/bin/Origami.SMTP.sh
cp ../license.txt origami-smtp_$version/usr/bin/license.txt
cp ../VERSION origami-smtp_$version/usr/bin/VERSION
sed -i "s/{version}/$version/g" debian-control
cp debian-control origami-smtp_$version/DEBIAN/control
cp origami-smtp.desktop origami-smtp_$version/usr/share/applications/origami-smtp.desktop
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
