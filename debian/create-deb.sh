#!/bin/sh
cd ./debian
version=`cat ../VERSION`
mkdir origami-smtp_v$version
mkdir origami-smtp_v$version/DEBIAN
mkdir -p origami-smtp_v$version/etc/menu
mkdir -p origami-smtp_v$version/usr/bin
mkdir -p origami-smtp_v$version/etc/ssl/certs
mkdir -p origami-smtp_v$version/usr/share/applications
cp ../Origami\ SMTP.jar ./Origami.SMTP.jar
./jar2sh.sh linux-launch.config
cp Origami.SMTP.jar origami-smtp_v$version/usr/bin/Origami.SMTP.jar
cp Origami.SMTP.sh origami-smtp_v$version/usr/bin/Origami.SMTP.sh
cp debian-control origami-smtp_v$version/DEBIAN/control
cp origami-smtp.desktop origami-smtp_v$version/usr/share/applications/origami-smtp.desktop
cat ../Origami_CA.crt > origami-smtp_v$version/etc/ssl/certs/Origami_CA.pem
dpkg-deb --build origami-smtp_v$version
rm -rf origami-smtp_v1.0.0$version
mv origami-smtp_v$version.deb ../
cd ..
if test -e "origami-smtp_v$version.deb"; then
	echo "origami-smtp_v$version.deb created"
    return 0
else
    return 1
fi
