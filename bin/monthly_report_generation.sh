#!/bin/bash
# Purpose:  Create monthly QvExport file for reporting team.
#

for var in $@
do
    case "$var" in
        --help) HELP=true ;;
        -env=*) ENVIRONMENT=`echo $var | cut -f2 -d\=` ;;
    esac
done

sleepAmount=30

PROPS_FILE_LOCATION="properties/$ENVIRONMENT.properties"
OUTPUT_LOG_FILE=/tmp/monthly_report_generation.log

function help() {

    echo -en "\n\nOPTIONS:";
    echo -en "\n\t-env=[dev, test, prod]        REQUIRED: specify environment specific java system properties (as per properties directory at the root of this project)"
    echo -en "\n\t--help                        This help manual"
    echo -en "\n\nEXAMPLES:";
    echo -en "\n\t./bin/monthly_report_generation.sh -env=prod          :   initiate QvEx[prt for all students in prod environment \n\n"
}

function readPropertiesFile() {

    # source properties file

  if [ -f "$PROPS_FILE_LOCATION" ]
  then
    echo "$PROPS_FILE_LOCATION found."

    while IFS='=' read -r key value
    do
      key=$(echo $key | tr '.' '_')
      key=$(echo $key | tr ':' '_')
      eval "${key}='${value}'" > /dev/null 2>&1;

    done < "$PROPS_FILE_LOCATION"

  else
    echo "$PROPS_FILE_LOCATION not found."
  fi

}

function callQvExport() {
    echo -en "\n\ncallQvExport() db userId = $lms_transactional_username\n";
    mysql -u $lms_transactional_username -p$lms_transactional_password -h $HOSTNAME lms_transactional -e 'call refresh_QvExport;'
}


function exportQvExport() {
mysql -u $lms_transactional_username -p$lms_transactional_password -h $HOSTNAME lms_transactional -e 'select * from lms_transactional.QvExport INTO OUTFILE '/tmp/qvexport.csv' FIELDS ENCLOSED BY '' TERMINATED BY '\t' ESCAPED BY '' LINES TERMINATED BY '\r\n';'
}


if [ ! -z "$HELP" ]; then
    help
else
    readPropertiesFile
    echo "Calling procedure QvExport"
    callQvExport
    sleep $sleepAmount
    echo "Exporting QvExport to csv"
    exportQvExport
    sleep $sleepAmount
fi
