#!/bin/sh
# Bristlecone-0.7
#
# Bristlecone Cluster Test Tools
# (c) 2006-2007 Continuent, Inc.. All rights reserved.

BHOME=`dirname $0`/..

# Load all jars from the lib and lib-ext directories.
for jar in $BHOME/lib/*.jar $BHOME/lib-ext/*.jar
do
  if [ -z $CP ]; then
    CP=$jar
  else
    CP=$CP:$jar
  fi
done
CP=$CP:$BHOME/config
CLUSTER_HOME_ARGS=""
CLUSTER_HOME="$BHOME/../cluster-home"
if [ -d "$CLUSTER_HOME" ]; then
        clusterHomeDir="`cd $CLUSTER_HOME;pwd`"
        echo "Using $clusterHomeDir as cluster.home"
        CLUSTER_HOME_ARGS="-Dcluster.home=$clusterHomeDir"
fi

CP=$CP:$CLUSTER_HOME/conf
CP=$CP:$BHOME/config


BRISTLECONE_JVMDEBUG_PORT=54001
# uncomment to debug
# JVM_OPTIONS="${JVM_OPTIONS} -enableassertions -Xdebug -Xnoagent -Dwrapper.java.pid=$$ -Dtungsten.router.name=evaluator -Djava.compiler=none -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$BRISTLECONE_JVMDEBUG_PORT"

java -cp $CP ${JVM_OPTIONS} $CLUSTER_HOME_ARGS com.continuent.bristlecone.evaluator.Evaluator $*
