#!/bin/bash
# (C) Copyright 2012,2013 Continuent, Inc - Released under the New BSD License
# Version 1.0.5 - 2013-04-03

CURDIR=`dirname $0`
if [ -f $CURDIR/COMMON_NODES.sh ]
then
    . $CURDIR/COMMON_NODES.sh
else
    export NODE1=
    export NODE2=
    export NODE3=
    export NODE4=
fi

export ALL_NODES=($NODE1 $NODE2 $NODE3 $NODE4)
# indicate which servers will be masters, and which ones will have a slave service
# in case of all-masters topologies, these two arrays will be the same as $ALL_NODES
# These values are used for automated testing

# for all-masters and star replication
export MASTERS=($NODE1 $NODE2 $NODE3 $NODE4)
export SLAVES=($NODE1 $NODE2 $NODE3 $NODE4)
export HUB=$NODE3

# MMSERVICES are the names used for services when installing multiple masters
export MM_SERVICES=(alpha bravo charlie delta echo foxtrot golf hotel)
export HUB_SERVICE=charlie


