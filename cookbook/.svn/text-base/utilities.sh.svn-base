#!/bin/bash
# (C) Copyright 2012,2013 Continuent, Inc - Released under the New BSD License
# Version 1.0.5 - 2013-04-03

cookbook_dir=$(dirname $0)

if [ ! -f $cookbook_dir/BOOTSTRAP.sh ]
then
    echo "$cookbook_dir/BOOTSTRAP.sh not found"
    exit 1
fi
. $cookbook_dir/BOOTSTRAP.sh COMMON_NODES.sh


function fill_roles {
    SLAVE_COUNT=0
    MASTER_COUNT=0
    SLAVES=()
    MASTERS=()
    for NODE in ${ALL_NODES[*]} 
    do 
        for role in $($TREPCTL -host $NODE services |grep role | awk '{print $3}')
        do
            if [ "$role" == "master" ]
            then
                MASTERS[$MASTER_COUNT]=$NODE
                MASTER_COUNT=$(($MASTER_COUNT+1))
            fi
            if [ "$role" == "slave" ]
            then
                SLAVE_EXISTS=$(echo ${SLAVES[*]} | grep -w $NODE)
                if [ -z "$SLAVE_EXISTS" ]
                then
                    SLAVES[$SLAVE_COUNT]=$NODE
                    SLAVE_COUNT=$(($SLAVE_COUNT+1))
                fi
            fi
        done
    done
    export  MASTERS=(${MASTERS[*]})
    export SLAVES=(${SLAVES[*]})
}

function clear_node {
	
		NODE=$1
		# MYSQL is defined in BOOTSTRAP.sh
		ssh $NODE "if [ ! -d $TUNGSTEN_BASE ] ; then mkdir -p $TUNGSTEN_BASE ;  fi" 
	    ssh $NODE "if [ -x $REPLICATOR ] ; then $REPLICATOR stop;  fi" 
	    ssh $NODE rm -rf $TUNGSTEN_BASE/*  
	    for D in $($MYSQL -h $NODE -BN -e 'show schemas like "tungsten%"' )
	    do
	        $MYSQL -h $NODE -e "drop schema $D"
	    done
	    $MYSQL -h $NODE -e 'drop schema if exists test'
	    $MYSQL -h $NODE -e 'drop schema if exists evaluator'
	    $MYSQL -h $NODE -e 'create schema test'
	    $MYSQL -h $NODE -e 'set global read_only=0'
	    $MYSQL -h $NODE -e 'set global binlog_format=mixed'
	    $MYSQL -h $NODE -e 'reset master'	
	
}

diff(){
    a1="$1"
    a2="$2"
    awk -va1="$a1" -va2="$a2" '
     BEGIN{
       m= split(a1, A1," ")
       n= split(a2, t," ")
       for(i=1;i<=n;i++) { A2[t[i]] }
       for (i=1;i<=m;i++){
            if( ! (A1[i] in A2)  ){
                printf A1[i]" "
            }
        }
    }'
}

#
# Loops through all the services in the configuration files
# Prints a label and value for a given property
# INPUT: 
# #1: Configuration directory
# #2: an user-defined label for that value
# #3: The property to look for in the configuration file
# #4: (optional) if set, will only print the value, not the labeland service info
function get_property_value
{
    CONF_DIR=$1
    LABEL=$2
    PROPERTY=$3
    VALUE_ONLY=$4
    for F in $CONF_DIR/static-*.properties
    do
        SERVICE=$(echo $F | perl -ne 'print $1 if /static-(\w+).properties/' )
        ACTION_STR="print \$1,\$/ if /^$PROPERTY=(.*)/"
        for VALUE in $(perl -ne "$ACTION_STR" $F)
        do
            if [ -n "$VALUE_ONLY" ]
            then
                echo $VALUE
            else
                printf "%15s : (service: %s) %s\n" $LABEL $SERVICE $VALUE
            fi
        done
    done
}

# Prints a value for a given property
# INPUT: 
# #1: Configuration directory
# #2: The property to look for in the configuration file
# #3: The service for the configuration file
function get_specific_property_value
{
    CONF_DIR=$1
    PROPERTY=$2
    SERVICE=$3
    F=$CONF_DIR/static-$SERVICE.properties
    ACTION_STR="print \$1,\$/ and exit if /^$PROPERTY=(.*)/"
    perl -ne "$ACTION_STR" $F
}


function remote_file_exists
{
    NODE=$1
    FILENAME=$2
    ATTRIBUTE=$3
    [ -z "$ATTRIBUTE" ] && ATTRIBUTE='-e'
    EXISTS=$(ssh $NODE "if [ $ATTRIBUTE $FILENAME ] ; then echo 'yes' ; fi ")
    echo $EXISTS
}

function find_used_serviceName {

    USED_SERVICE_COUNT=0
    USED_SERVICE=()
    for NODE in ${ALL_NODES[*]} 
    do 
        for serviceName in $($TREPCTL -host $NODE services |grep serviceName  | awk '{print $3}')
        do
        	
    		USED_SERVICE[$USED_COUNT]=$serviceName
    		USED_COUNT=$(($USED_COUNT+1))
		done
    done
    USED_SERVICE_U=$(echo "${USED_SERVICE[*]}"|tr " " "\n"|sort|uniq|tr "\n" " ")
    export USED_SERVICE=(${USED_SERVICE_U[*]})
}
