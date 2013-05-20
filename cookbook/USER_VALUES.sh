#!/bin/bash
# (C) Copyright 2012,2013 Continuent, Inc - Released under the New BSD License
# Version 1.0.5 - 2013-04-03

# User defined values for the cluster to be installed.

# Where to install Tungsten Replicator
export TUNGSTEN_BASE=$HOME/installs/cookbook

# Directory containing the database binary logs
export BINLOG_DIRECTORY=/var/lib/mysql

# Path to the script that can start, stop, and restart a MySQL server
export MYSQL_BOOT_SCRIPT=/etc/init.d/mysql

# Path to the options file
export MY_CNF=/etc/my.cnf

# Database credentials
export DATABASE_USER=tungsten
export DATABASE_PASSWORD=secret
export DATABASE_PORT=3306

# Name of the service to install
export TUNGSTEN_SERVICE=cookbook

# Replicator ports
export RMI_PORT=10000
export THL_PORT=2112

# If set, replicator starts after installation
[ -z "$START_OPTION" ] && export START_OPTION=start

##############################################################################
# Options used by the "direct slave " installer only
# Modify only if you are using 'install_master_slave_direct.sh'
##############################################################################
export DIRECT_MASTER_BINLOG_DIRECTORY=$BINLOG_DIRECTORY
export DIRECT_SLAVE_BINLOG_DIRECTORY=$BINLOG_DIRECTORY
export DIRECT_MASTER_MY_CNF=$MY_CNF
export DIRECT_SLAVE_MY_CNF=$MY_CNF
##############################################################################

##############################################################################
# Variables used when removing the cluster
# Each variable defines an action during the cleanup
##############################################################################
[ -z "$STOP_REPLICATORS" ]            && export STOP_REPLICATORS=1
[ -z "$REMOVE_TUNGSTEN_BASE" ]        && export REMOVE_TUNGSTEN_BASE=1
[ -z "$REMOVE_SERVICE_SCHEMA" ]       && export REMOVE_SERVICE_SCHEMA=1
[ -z "$REMOVE_TEST_SCHEMAS" ]         && export REMOVE_TEST_SCHEMAS=1
[ -z "$REMOVE_DATABASE_CONTENTS" ]    && export REMOVE_DATABASE_CONTENTS=0
[ -z "$CLEAN_NODE_DATABASE_SERVER" ]  && export CLEAN_NODE_DATABASE_SERVER=1
##############################################################################

