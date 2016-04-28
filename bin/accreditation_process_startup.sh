# Purpose:  Run Accreditation Process as a standalone java application

# Usage
#   ./bin/accreditation_process_startup.sh -env=dev -f          :   start accred process in the foreground using dev environment properties
#   ./bin/accreditation_process_startup.sh -env=prod            :   start accred process in the background using prod environment properties

for var in $@
do
    case "$var" in
        -u) UNDEPLOY=true ;;
        -f) FOREGROUND=true ;;
        -env=*) ENVIRONMENT=`echo $var | cut -f2 -d\=` ;;
    esac
done


ACCREDITATION_PROCESS_HOME=accreditation_process
DEPS_DIR=target/dependencies
CLASSES_DIR=target/classes
CAMEL_CONTEXT_PATH="spring/accreditation-camel-context.xml"
PROPS_FILE_LOCATION="$ACCREDITATION_HOME../properties/$ENVIRONMENT.properties"
OUTPUT_LOG_FILE=/opt/jboss/eap/jboss-eap-6.4/standalone/log/accreditation_process.log

function checkPreReqs() {

    if [[ ! $(ls -A .projectRoot) ]];
    then
        echo -en "\nExecute this from the root directory of this project\n\n"
        exit 1;
    else
        echo -en "\nExecuting from correct directory: `pwd` \n\n"
    fi

    if [ "x$ENVIRONMENT" == "x" ]; then
        echo -en "must pass parameter: -env=<environment> . \n\n"
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

    # JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y"
    if [ ! -z "$FOREGROUND" ]; then
        java -classpath $DEPS_DIR/*:$CLASSES_DIR $JAVA_OPTS -Dcamel_context_path=$CAMEL_CONTEXT_PATH -Dprops_file_location=$PROPS_FILE_LOCATION com.redhat.gpe.accreditation.util.BootStrap
    else
        echo -en "\nStarting accreditation_process.  Check logs at $OUTPUT_LOG_FILE\n\n"
        nohup java -classpath $DEPS_DIR/*:$CLASSES_DIR $JAVA_OPTS -Dcamel_context_path=$CAMEL_CONTEXT_PATH -Dprops_file_location=$PROPS_FILE_LOCATION com.redhat.gpe.accreditation.util.BootStrap > $OUTPUT_LOG_FILE 2>&1 &
    fi
}

checkPreReqs
buildAndStart
