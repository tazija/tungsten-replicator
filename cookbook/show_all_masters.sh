#!/bin/bash
# (C) Copyright 2012,2013 Continuent, Inc - Released under the New BSD License
# Version 1.0.5 - 2013-04-03
cookbook_dir=$(dirname $0)
echo "This script is DEPRECATED - You can simply use $cookbook_dir/show_cluster"
$cookbook_dir/show_cluster NODES_ALL_MASTERS.sh
