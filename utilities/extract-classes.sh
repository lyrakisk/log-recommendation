#!/usr/bin/env bash

TEMP="temp"
SOURCE="$1"
DESTINATION="$2"

mkdir -p $DESTINATION
rm -rf $TEMP
mkdir -p $TEMP
cp -r $SOURCE $TEMP

# remove test classes
cd $TEMP
rm -rf $(find . -name '*test*')
rm -rf $(find . -name '*Test*')

# copy java classes
cp $(find . -name '*.java')  ../$DESTINATION

# delete temp folder
cd ..
rm -rf $TEMP

