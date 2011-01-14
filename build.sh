#!/bin/bash

function build-rpm {
	ant clean
	ant -Dswig.location=$SWIG_LOCATION -Djdk.location=$JDK_LOCATION -Dlcmaps.location=$LCMAPS_LOCATION -Dlcmaps-without-gsi.location=$LCMAPS_WITHOUT_GSI_LOCATION -Dlcmaps-plugins-basic.location=$LCMAPS_BASIC_PLUGIN_LOCATION -Dlcmaps-plugins-voms.location=$LCMAPS_VOMS_PLUGIN_LOCATION -Dlibacl-devel.location=$LIBACL_LOCATION -Dlibattr-devel.location=$LIBATTR_LOCATION -Dxfsprogs-devel.location=$XFS_PROGS_LOCATION -Dversion=1.6.0  build-rpm
}	

function jar {
	ant clean
	ant -Dswig.location=$SWIG_LOCATION -Djdk.location=$JDK_LOCATION -Dlcmaps.location=$LCMAPS_LOCATION -Dlcmaps-without-gsi.location=$LCMAPS_WITHOUT_GSI_LOCATION -Dlcmaps-plugins-basic.location=$LCMAPS_BASIC_PLUGIN_LOCATION -Dlcmaps-plugins-voms.location=$LCMAPS_VOMS_PLUGIN_LOCATION -Dlibacl-devel.location=$LIBACL_LOCATION -Dlibattr-devel.location=$LIBATTR_LOCATION -Dxfsprogs-devel.location=$XFS_PROGS_LOCATION -Dversion=1.6.0  storm-backend.jar
}	

function deploy {
	 scp ./build/storm-backend.jar root@omii003-vm03.cnaf.infn.it:/opt/storm/backend/

}


## main

set -e

nargs=$#

if [nargs -ne 1] 
then 
	echo "You have to specify the target: build-rpm, jar, deploy"
	exit -1
fi	 

case "$1" in
        build-rpm)
            build-rpm
            ;;
         
        jar)
            jar
            ;;
         
        deploy)
            deploy
            ;;
        *)
            echo $"Usage: $0 {build-rpm|jar|deploy}"
            exit 1
 
esac