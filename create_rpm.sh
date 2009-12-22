#!/bin/bash

if [ -z $GPFS_HOME ]; then
    GPFS_HOME=/usr/lpp/mmfs
fi
export GPFS_HOME

if [ ! -d ./RPMS ]; then
    mkdir RPMS
fi

if [ ! -d ./RPMS/BUILD ]; then
    mkdir ./RPMS/BUILD
fi

if [ ! -d ./RPMS/RPMS ]; then
    mkdir ./RPMS/RPMS
fi

if [ ! -d ./RPMS/SOURCES ]; then
    mkdir ./RPMS/SOURCES
fi

if [ ! -d ./RPMS/SPECS ]; then
    mkdir ./RPMS/SPECS
fi

if [ ! -d ./RPMS/SRPMS ]; then
    mkdir ./RPMS/SRPMS
fi

# Retrieving version from spec file
VERSION=`cat ./rpm/storm-backend.spec | grep "Version:" | awk '{ print $2 }'`

echo "Building RPMs for version: $VERSION (retrieved from spec file: "./rpm/storm-backend.spec")"

if [ -z $VERSION ]; then 
    echo "Unable to retrieve the version from the spec file"
    exit 1
fi

# Get sources
ant -Dversion=$VERSION tar.src

CUR_DIR=`pwd`

cp $CUR_DIR/rpm/storm-backend.spec $CUR_DIR/RPMS/SPECS/storm-backend.spec
cp storm-backend-$VERSION.tar.gz ./RPMS/SOURCES/

# Generate RPMs
rpmbuild --define "_topdir $CUR_DIR/RPMS" -ba $CUR_DIR/RPMS/SPECS/storm-backend.spec

