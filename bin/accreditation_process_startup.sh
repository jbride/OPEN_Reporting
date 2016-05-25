#!/bin/sh

# Purpose:  Run Accreditation Process as a standalone java application

for var in $@
do
    case "$var" in
        -f) FOREGROUND=true ;;
        -d) DEBUG=true ;;
        --help) HELP=true ;;
        -env=*) ENVIRONMENT=`echo $var | cut -f2 -d\=` ;;
    esac
done

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`
RESOLVED_PROJECT_HOME=`cd "$DIRNAME/.."; pwd`
cd $RESOLVED_PROJECT_HOME

ACCREDITATION_PROCESS_HOME=accreditation_process
DEPS_DIR=target/dependencies
CLASSES_DIR=target/classes
CAMEL_CONTEXT_PATH="spring/accreditation-camel-context.xml"
PROPS_FILE_LOCATION="$RESOLVED_PROJECT_HOME/properties/$ENVIRONMENT.properties"
OUTPUT_LOG_FILE=/tmp/accreditation_process.log

function help() {
    
    echo -en "\n\nOPTIONS:";
    echo -en "\n\t-env=[dev, test, prod]        REQUIRED: specify environment specific java system properties (as per properties directory at the root of this project)"
    echo -en "\n\t-f                            run operating system process in foreground; default = false"
    echo -en "\n\t                                if process is run in background, then log will be directored to: $OUTPUT_LOG_FILE"
    echo -en "\n\t-d                            enable Java debugger; default = false"
    echo -en "\n\t--help                        this help manual"
    echo -en "\n\nEXAMPLES:";
    echo -en "\n\t./bin/accreditation_process_startup.sh -env=dev -f          :   start accred process in the foreground using dev environment properties"
    echo -en "\n\t./bin/accreditation_process_startup.sh -env=test -f -d      :   start accred process in the foreground using test environment properties and Java debugger enabled"
    echo -en "\n\t./bin/accreditation_process_startup.sh -env=prod            :   start accred process in the background using prod environment properties\n\n"
}


function checkPreReqs() {

    echo -en "\n\nDIRNAME = $DIRNAME : PROGNAME = $PROGNAME : RESOLVED_PROJECT_HOME = $RESOLVED_PROJECT_HOME\n"
    pwd

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

function buildAndStart() {
    cd $ACCREDITATION_PROCESS_HOME

    mvn clean package -DskipTests
    if [ $? != 0 ];
    then
        echo "Failed build." >&2;
        exit 1;
    fi

    mvn dependency:copy-dependencies -DoutputDirectory=$DEPS_DIR
    if [ $? != 0 ];
    then
        echo "Failed pulling of dependencies" >&2;
        exit 1;
    fi

    if [ ! -z "$DEBUG" ]; then
        JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y"
    fi

    if [ ! -z "$FOREGROUND" ]; then
        java -classpath $DEPS_DIR/*:$CLASSES_DIR $JAVA_OPTS -Dcamel_context_path=$CAMEL_CONTEXT_PATH -Dprops_file_location=$PROPS_FILE_LOCATION com.redhat.gpe.accreditation.util.BootStrap
    else
        echo -en "\nStarting accreditation_process.  Check logs at $OUTPUT_LOG_FILE\n\n"
        nohup java -classpath $DEPS_DIR/*:$CLASSES_DIR $JAVA_OPTS -Dcamel_context_path=$CAMEL_CONTEXT_PATH -Dprops_file_location=$PROPS_FILE_LOCATION com.redhat.gpe.accreditation.util.BootStrap > $OUTPUT_LOG_FILE 2>&1 &
    fi
}

if [ ! -z "$HELP" ]; then
    help
else
    checkPreReqs
    buildAndStart
fi
