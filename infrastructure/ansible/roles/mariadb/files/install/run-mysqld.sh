#!/bin/bash

SCRIPT_DIR=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
HOME=var/lib/mysql

source $SCRIPT_DIR/scripts/common.sh

set -eu

initialize_database
shutdown_local_mysql
unset_env_vars  
 
