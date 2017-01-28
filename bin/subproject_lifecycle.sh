#!/bin/bash

# Purpose:
#  1) build and deploy all OPEN_Reporting sub-projects (exception being accreditation_process sub-project) to a Fuse on JBoss EAP6.4 runtime environment
#
# Pre-res
#   1)  Ensure that JBoss EAP 6.4 is running with specified port-offset


for var in $@
do
    case "$var" in
        -u) UNDEPLOY=true ;;
        -env=*) ENVIRONMENT=`echo $var | cut -f2 -d\=` ;;
        -h) HELP=true ;;
        -help) HELP=true ;;
    esac
done

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`
RESOLVED_ROOT_PROJECT_HOME=`cd "$DIRNAME/.."; pwd`
cd $RESOLVED_ROOT_PROJECT_HOME

PROPS_FILE_LOCATION="properties/$ENVIRONMENT.properties"

function help() {
    echo -en "\n\nOPTIONS:";
    echo -en "\n\t-env=[dev, test, prod]        REQUIRED: specify environment specific java system properties (as per properties directory at the root of this project)"
    echo -en "\n\t-u                            undeploy subproject artifacts from JBoss runtime"
    echo -en "\n\t-h                            this help manual\n\n"
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

    if [ "x$ENVIRONMENT" == "x" ]; then
            echo -en "must pass parameter: -env=<environment> . \n\n"
            help
            exit 1;
    fi

}

function deployJBossModules() {
    gpteModulePath=$jboss_home/modules/system/layers/base/com/redhat/gpte/main
    echo -en "deployJBossModules() \n\n"
    if [ "$UNDEPLOY" != true ];
    then
        # 0) ensure fuse on jboss eap installation exists
        if [[ ! $(ls $jboss_home/modules/system/layers/fuse) ]];
        then
            echo -en "\nNot able to find fuse on jboss eap installation at: $jboss_home\n\n"
            exit 1
        else
            echo -en "\nfuse on jboss eap installation found at: $jboss_home\n\n"
        fi

        # 1) build gpte commons subprojects
        cd $RESOLVED_ROOT_PROJECT_HOME/commons
        mvn clean install -DskipTests
        cd $RESOLVED_ROOT_PROJECT_HOME

        # 2) prep JBOSS Modules
        #echo "layers=fuse,soa,gpte" > $jboss_home/modules/layers.conf
        rm -rf $gpteModulePath
        # cp $jboss_home/modules/system/layers/fuse/org/apache/commons/lang3/main/commons-lang3-3.2.1.jar $jboss_home/modules/system/layers/base/com/redhat/gpte/main  # org.apache.commons.lang3.StringUtils

        # 3) copy module.xml files
        cp -r config/modules/ $jboss_home/

        # 4) copy gpte libraries
        cp commons/domain/target/gpte-*.jar $gpteModulePath
        #cp commons/dao/target/gpte-*.jar $gpteModulePath
        #cp commons/services/target/gpte-*.jar $gpteModulePath

        # 5) reload JBoss EAP
        $jboss_home/bin/jboss-cli.sh --connect --controller=localhost:10124 :reload

        sleep 5 
    fi
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

function deployShared() {
    echo -en "deployShared() \n\n"
    cd $RESOLVED_ROOT_PROJECT_HOME/gpte_shared_process
    if [ "$UNDEPLOY" = true ];
    then
        mvn jboss-as:undeploy -Dcli.port=$cli_port
    else
        mvn jboss-as:deploy -DskipTests -Denvironment=dev -Dcli.port=$cli_port
    fi
    if [ $? != 0 ];
    then
        echo "Failed gpte_shared_process deploy." >&2;
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

if [ ! -z "$HELP" ]; then
    help
else
    ensurePreReqs
    readPropertiesFile
    checkRemotePort
    #deployJBossModules
    deployCC
    deploySReg
    deployShared
fi
