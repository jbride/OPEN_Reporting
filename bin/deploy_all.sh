#!/bin/bash

# Purpose:
#  1) build and deploy all OPEN_Reporting sub-projects (exception being accreditation_process sub-project) to a Fuse on JBoss EAP6.4 runtime environment
#
# Pre-res
#   1)  Ensure that JBoss EAP 6.4 is running with specified port-offset

# Usage
#   Deploy all:    ./bin/deploy_all.sh -env=dev
#   UnDeploy all:  ./bin/deploy_all.sh -u

for var in $@
do
    case "$var" in
        -u) UNDEPLOY=true ;;
        -env=*) ENVIRONMENT=`echo $var | cut -f2 -d\=` ;;
    esac
done

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`
RESOLVED_ROOT_PROJECT_HOME=`cd "$DIRNAME/.."; pwd`
cd $RESOLVED_ROOT_PROJECT_HOME

PROPS_FILE_LOCATION="properties/$ENVIRONMENT.properties"

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


# Test remote host:port availability (TCP-only as UDP does not reply)
function checkRemotePort() {
    (echo >/dev/tcp/$jboss_eap_host/$cli_port) &>/dev/null
    if [ $? -eq 0 ]; then
        echo -en "\n$jboss_eap_host:$cli_port is open.\n"
    else
        echo -en "\n$jboss_eap_host:$cli_port is closed.\n"
        exit 1; 
    fi
}

function ensurePreReqs() {

    echo -en "\n\nDIRNAME = $DIRNAME : PROGNAME = $PROGNAME : RESOLVED_ROOT_PROJECT_HOME = $RESOLVED_ROOT_PROJECT_HOME\n"
    if [[ ! $(ls -A .projectRoot) ]];
    then
        echo -en "\nExecute this from the root directory of this project\n\n"
        help
        exit 1;
    else
        echo -en "\nExecuting from correct directory: `pwd` \n\n"
    fi

    if [ "x$UNDEPLOY" == "x" ]; then
        if [ "x$ENVIRONMENT" == "x" ]; then
            echo -en "must pass parameter: -env=<environment> . \n\n"
            exit 1;
        fi
    fi

    checkRemotePort
}


function deployCC() {
    echo -en "deployCC() \n\n"
    cd $RESOLVED_ROOT_PROJECT_HOME/course_completion_process
    if [ "$UNDEPLOY" = true ];
    then
        mvn jboss-as:undeploy -Dcli.port=$cli_port
    else
        mvn jboss-as:deploy -DskipTests -Denvironment=dev -Dcli.port=$cli_port
    fi
    if [ $? != 0 ];
    then
        echo "Failed course_completion_process (un)deploy." >&2;
        exit 1;
    fi
}

function deployUP() {
    echo -en "deployUP() \n\n"
    cd $RESOLVED_ROOT_PROJECT_HOME/gpte_universal_process
    if [ "$UNDEPLOY" = true ];
    then
        mvn jboss-as:undeploy -Dcli.port=$cli_port
    else
        mvn jboss-as:deploy -DskipTests -Denvironment=dev -Dcli.port=$cli_port
    fi
    if [ $? != 0 ];
    then
        echo "Failed gpte_uniersal_process deploy." >&2;
        exit 1;
    fi
}

function deploySReg() {
    echo -en "deploySReg() \n\n"
    cd $RESOLVED_ROOT_PROJECT_HOME/student_registration_process
    if [ "$UNDEPLOY" = true ];
    then
        mvn jboss-as:undeploy -Dcli.port=$cli_port
    else
        mvn jboss-as:deploy -DskipTests -Denvironment=dev -Dcli.port=$cli_port
    fi
    if [ $? != 0 ];
    then
        echo "Failed student_registration_process deploy." >&2;
        exit 1;
    fi
}

readPropertiesFile
ensurePreReqs
deployCC
deployUP
deploySReg
