#!/bin/bash
# (C) Copyright 2012,2013 Continuent, Inc - Released under the New BSD License
# Version 1.0.5 - 2013-04-03
cookbook_dir=$(dirname $0)

if [ ! -f $cookbook_dir/../CURRENT_TOPOLOGY ]
then
    echo "This command requires an installed cluster"
    exit 1
fi

TOPOLOGY=$(echo $(cat CURRENT_TOPOLOGY) | tr '[a-z]' '[A-Z]')

NODES=NODES_$TOPOLOGY.sh

if [ ! -f $cookbook_dir/$NODES ]
then
    echo "$cookbook_dir/$NODES not found"
    exit 1
fi
if [ ! -f $cookbook_dir/BOOTSTRAP.sh ]
then
    echo "$cookbook_dir/BOOTSTRAP.sh not found"
    exit 1
fi

if [ ! -f $cookbook_dir/utilities.sh ]
then
    echo "$cookbook_dir/utilities.sh not found"
    exit 1
fi

. $cookbook_dir/BOOTSTRAP.sh $NODES
. $cookbook_dir/utilities.sh

SUPPORTED_TOOLS="help readme paths configure_service backups copy_backup query_node query_all_nodes tungsten_service insert_retrieve trepctl thl replicator heartbeat services log vilog vimlog emacslog conf vimconf emacsconf"
CONF_DIR="$TUNGSTEN_BASE/tungsten/tungsten-replicator/conf/"

if [ -z "$1" ]
then
    echo "No tool or service specified. Require one of '$SUPPORTED_TOOLS'"
    exit 1
fi

function query_node
{
    node=$1
    shift
    query="$@"
    $MYSQL -h $node -e "$query"
}

function query_all_nodes
{
    query="$@"
    for node in ${ALL_NODES[*]}
    do
        query_node $node "$query"
    done
}

function get_tungsten_service
{
    node=$1
    service=$2
    if [ -n "$service" ]
    then
        query="select seqno, source_id, applied_latency,shard_id, update_timestamp,extract_timestamp from tungsten_$service.trep_commit_seqno"
        echo "# node: $node - service: $service"
        query_node $node "$query"
    else
        for service in $($TREPCTL -host $node services | grep serviceName | awk '{print $3}')     
        do
            get_tungsten_service $node $service
        done       
    fi
}

function tungsten_service
{
    operation=$1
    shift
    if [ -z "$operation" ]
    then
        echo "syntax: tungsten_service {node|all} [service]"
        exit 1
    fi
    case $operation in
        node)
            node=$1
            shift
            if [ -z "$node" ]
            then
                echo "node required"
                exit 1
            fi
            get_tungsten_service $node $@
            ;;
        all)
            for node in ${ALL_NODES[*]}
            do
                get_tungsten_service $node $@
            done
            ;;
        *)
            echo "unrecognized operation: $operation "
            echo "syntax: tungsten_service {node|all} [service]"
            exit 1
            ;;
    esac
}

function show_paths
{
    for BIN in replicator trepctl thl
    do
        printf "%15s : %s\n" $BIN "$TUNGSTEN_BASE/tungsten/tungsten-replicator/bin/$BIN"
    done
    printf "%15s : %s\n" 'log' "$TUNGSTEN_BASE/tungsten/tungsten-replicator/log/trepsvc.log"
    printf "%15s : %s\n" 'service-cfg' "$TUNGSTEN_BASE/tungsten/tools/configure-service"
    printf "%15s : %s\n" 'conf' $CONF_DIR
    get_property_value $CONF_DIR 'thl-dir' 'replicator.store.thl.log_dir'
    get_property_value $CONF_DIR 'backup-dir' 'replicator.storage.agent.fs.directory'
    get_property_value $CONF_DIR 'backup-agent' 'replicator.backup.default'
    shift 
    if [ -n "$1" ]
    then
        get_property_value $CONF_DIR $1 $1
    fi
}

function show_backups
{
    get_property_value $CONF_DIR 'backup-agent' 'replicator.backup.default'
    get_property_value $CONF_DIR 'backup-dir' 'replicator.storage.agent.fs.directory' 
    for DIR in $(get_property_value $CONF_DIR '0' 'replicator.storage.agent.fs.directory' 1) 
    do
        echo $(dirname $DIR) >> dirs$$
    done
    for DIR in $(sort dirs$$ | uniq)
    do
        for NODE in ${ALL_NODES[*]}
        do
            HOW_MANY=$(ssh $NODE find $DIR -type f | wc -l)
            echo "# [node: $NODE] $HOW_MANY files found"
            if [ "$HOW_MANY" != "0" ]
            then
                for SUBDIR in $(ssh $NODE ls -d "$DIR/*")
                do
                    HOW_MANY=$(ssh $NODE find $SUBDIR -type f | wc -l)
                    if [ "$HOW_MANY" != "0" ]
                    then
                        echo "++ $SUBDIR"
                        ssh $NODE ls -lh $SUBDIR
                    fi
                done
                echo ''
            fi
        done
    done
    rm dirs$$
}

function copy_backup_files
{
    SERVICE=$1
    SOURCE_NODE=$2
    DESTINATION_NODE=$3
    if [ -z "$DESTINATION_NODE" ]
    then
        echo "syntax: copy_backup SERVICE SOURCE_NODE DESTINATION_NODE"
        exit 1
    fi
    BACKUP_DIRECTORY=$( get_specific_property_value $CONF_DIR 'replicator.storage.agent.fs.directory'  $SERVICE)
    if [ "$(remote_file_exists $SOURCE_NODE $BACKUP_DIRECTORY '-d' )" != "yes" ]
    then
        echo "Backup directory $BACKUP_DIRECTORY not found in $SOURCE_NODE"
        exit 1
    fi
    if [ "$(remote_file_exists $DESTINATION_NODE $BACKUP_DIRECTORY '-d' )" != "yes" ]
    then
        echo "Backup directory $BACKUP_DIRECTORY not found in $DESTINATION_NODE"
        exit 1
    fi
    ssh $SOURCE_NODE "scp -pr $BACKUP_DIRECTORY/* $DESTINATION_NODE:$BACKUP_DIRECTORY/"
}

function insert_retrieve
{
    node1=$1
    node2=$2
    if [ -z "$node2" ]
    then
        echo "syntax: insert_retrieve node1 node2"
        exit 1
    fi
    table_name="test_retrieve$$"
    query1="drop table if exists test.$table_name"
    query2="create table test.$table_name ( i int)"
    query_node $node1 "$query1"
    sleep 2
    query_node $node1 "$query2"
    timeout=60
    elapsed=0
    while  [ $elapsed -lt $timeout ]
    do
        found=$(query_node $node2 "select count(*) from information_schema.tables where table_schema='test' and table_name='$table_name' "| grep -v "count")
        if [ "$found" == "1" ]
        then
            echo "Found table $table_name in node $node2 - Elapsed: $elapsed seconds"
            return
        fi
        elapsed=$(($elapsed+1))
        sleep 1
    done
    query_node $node1 "$query1"
    echo "table $table_name not found in node $node2"
    exit 1
}



ARG=$1
shift

case "$ARG" 
    in
    help)
        if [ -n "$1" ]
        then
            grep -w $1 $cookbook_dir/REFERENCE.txt
        else
            less $cookbook_dir/REFERENCE.txt
        fi
        ;;
    readme)
        less $cookbook_dir/README.txt
        ;;
    paths)
        show_paths $1
       ;;
    configure_service)
        $TUNGSTEN_BASE/tungsten/tools/configure-service $@
        ;;
    tungsten_service)
        tungsten_service $@
       ;;
    insert_retrieve)
        insert_retrieve "$@"
       ;;
    query_node)
        node=$1
        shift
        query_node $node "$@"
       ;;
    query_all_nodes)
        query_all_nodes "$@"
       ;;
    backups)
        show_backups
       ;;
    copy_backup)
        copy_backup_files $1 $2 $3
       ;;
    trepctl)
        $TREPCTL $@
        ;;
    services)
        $TREPCTL services
        ;;
    heartbeat)
        for NODE in ${MASTERS[*]}
        do
            $TREPCTL -host $NODE heartbeat
        done
        ;;
    thl)
        $THL $@
        ;;
    replicator)
       $REPLICATOR  $@
       ;;
    log)
       less $TUNGSTEN_BASE/tungsten/tungsten-replicator/log/trepsvc.log 
       ;;
    vilog)
       vi $TUNGSTEN_BASE/tungsten/tungsten-replicator/log/trepsvc.log 
       ;;
    vimlog)
       vim $TUNGSTEN_BASE/tungsten/tungsten-replicator/log/trepsvc.log 
       ;;
    emacslog)
       emacs $TUNGSTEN_BASE/tungsten/tungsten-replicator/log/trepsvc.log 
       ;;
    conf)
       less $TUNGSTEN_BASE/tungsten/tungsten-replicator/conf/static*.properties 
       ;;
    vimconf)
       vim -o $TUNGSTEN_BASE/tungsten/tungsten-replicator/conf/static*.properties 
       ;;
    emacsconf)
       emacs $TUNGSTEN_BASE/tungsten/tungsten-replicator/conf/static*.properties 
       ;;
    *)
        echo "Unknown tool requested. Valid choices are '$SUPPORTED_TOOLS'"
        exit 1
esac
