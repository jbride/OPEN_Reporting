#!/bin/bash
# Purpose:  Determine all StudentAccreditations from a fresh lms_transactional database of existing student courses
#
# Dependencies:
#  1)  accreditation-process JVM must be running on same machine and its rest-dsl endpoint available at:  http://$HOSTNAME:9090/gpte_accreditation/students/batch

for var in $@
do
    case "$var" in
        --help) HELP=true ;;
        -env=*) ENVIRONMENT=`echo $var | cut -f2 -d\=` ;;
    esac
done

sleepAmount=1
incrementAmount=1000

low_student_id=10000
max_student_id=35000

PROPS_FILE_LOCATION="properties/$ENVIRONMENT.properties"
OUTPUT_LOG_FILE=/tmp/accreditation_batch_evaluation.log


function help() {

    echo -en "\n\nOPTIONS:";
    echo -en "\n\t-env=[dev, test, prod]        REQUIRED: specify environment specific java system properties (as per properties directory at the root of this project)"
    echo -en "\n\t--help                        this help manual"
    echo -en "\n\nEXAMPLES:";
    echo -en "\n\t./bin/accreditation_process_startup.sh -env=dev -f          :   start accred process in the foreground using dev environment properties\n\n"
}


function checkPreReqs() {

    if [[ ! $(ls -A .projectRoot) ]];
    then
        echo -en "\nExecute this from the root directory of this project\n\n"
        help
        exit 1;
    else
        echo -en "\nExecuting from correct directory: `pwd` \n\n"
    fi

    if [ "x$ENVIRONMENT" == "x" ]; then
        echo -en "must pass parameter: -env=<environment> . \n\n"
        help
        exit 1;
    fi


}

function determineBatchAccreditations() {
  for (( ; ;  ))
  do

    if [[ $low_student_id < $max_student_id ]]; then

        high_student_id=`expr $low_student_id + $incrementAmount`

        echo -en "\ndetermining batch accreditations.  low_student_id = $low_student_id : high_student_id = $high_student_id\n";
        curl -v -X PUT  -H "ACCEPT: application/json" \
                -H "TEST_RULES_ONLY: false" \
                -H "RESPOND_JSON: false" \
                -H "LOW_STUDENT_ID: $low_student_id" \
                -H "HIGH_STUDENT_ID: $high_student_id" \
                http://$HOSTNAME:9090/gpte_accreditation/students/batch

        sleep $sleepAmount
        low_student_id=`expr $low_student_id + $incrementAmount`
    else
        exit 0;
    fi

  done
}

function resetData() {
    echo -en "\n\nresetData() db userId = $lms_transactional_username\n";
    mysql -u $lms_transactional_username -p$lms_transactional_password -h $HOSTNAME lms_transactional -e 'delete from StudentAccreditations;'
    mysql -u $lms_transactional_username -p$lms_transactional_password -h $HOSTNAME lms_transactional -e 'update StudentCourses set processed=0;'
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


if [ ! -z "$HELP" ]; then
    help
else
    checkPreReqs
    readPropertiesFile

    resetData
    determineBatchAccreditations
fi
