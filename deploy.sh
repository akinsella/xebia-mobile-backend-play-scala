#!/bin/sh
DIST_NAME=xebia-mobile-backend-play-scala
DIST_VERSION=1.0-SNAPSHOT
DIST=$DIST_NAME-$DIST_VERSION

echo "Creating dist"
play -Dconfig.file=conf/cloud.conf dist
cd dist
echo "Unzipping dist"
unzip $DIST.zip
echo "Copying certs"
cp -r ../cert $DIST
echo "Copying public resources"
cp -r ../public $DIST
echo "Zipping result"
zip $DIST.zip -r $DIST
cd ..
echo "Pushing to Cloudfoundry"
vmc push dist/$DIST.zip

