#!/bin/sh
# Bristlecone-0.7
#
# Cluster benchmark demonstration. 
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

BRISTLECONE_JVMDEBUG_PORT=54001
# uncomment to debug
# JVM_OPTIONS="${JVM_OPTIONS} -enableassertions -Xdebug -Xnoagent -Dwrapper.java.pid=$$ -Dtungsten.router.name=benchmark -Djava.compiler=none -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$BRISTLECONE_JVMDEBUG_PORT"

java -cp $CP ${JVM_OPTIONS} com.continuent.bristlecone.benchmark.BenchmarkLauncher $*
