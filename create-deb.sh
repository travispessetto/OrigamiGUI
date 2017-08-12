#!/bin/sh
mkdir origami-smtp_v1.0.0
mkdir origami-smtp_v1.0.0/DEBIAN
mkdir -p origami-smtp_v1.0.0/etc/menu
mkdir -p origami-smtp_v1.0.0/usr/bin
mkdir -p origami-smtp_v1.0.0/etc/ssl/certsi
mkdir -p origami-smtp_v1.0.0/usr/share/applications
mv Origami\ SMTP.jar Origami.SMTP.jar
./jar2sh.sh linux-launch.config
cp Origami.SMTP.jar origami-smtp_v1.0.0/usr/bin/Origami.SMTP.jar
cp Origami.SMTP.sh origami-smtp_v1.0.0/usr/bin/Origami.SMTP.sh
cp debian-control origami-smtp_v1.0.0/DEBIAN/control
cp origami-smtp.desktop origami-smtp_v1.0.0/usr/share/applications/origami-smtp.desktop
cat Origami_CA.crt > origami-smtp_v1.0.0/etc/ssl/certs/Origami_CA.pem
dpkg-deb --build origami-smtp_v1.0.0 
rm -rf origami-smtp_v1.0.0
