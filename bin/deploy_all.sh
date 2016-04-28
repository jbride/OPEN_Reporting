#!/bin/bash

# Usage
#   Deploy all:    ./bin/deploy_all.sh -env=dev
#   UnDeploy all:  ./bin/deploy_all.sh -u

PROJECT_HOME=`pwd`

LOCAL_DIR=`basename $PROJECT_HOME`
if [ "$LOCAL_DIR" != integration ];then
    echo "Need to execute from OPEN_ADMIN/integration directory"
    exit 1;
fi


for var in $@
do
    case "$var" in
        -u) UNDEPLOY=true ;;
        -env=*) ENVIRONMENT=`echo $var | cut -f2 -d\=` ;;
    esac
done

function ensurePreReqs() {
    
    echo -en "ensurePreReqs() \n\n"
    if [ "x$UNDEPLOY" == "x" ]; then
        if [ "x$ENVIRONMENT" == "x" ]; then
            echo -en "must pass parameter: -env=<environment> . \n\n"
            exit 1;
        fi
    fi
}


function deployCC() {
    echo -en "deployCC() \n\n"
    cd $PROJECT_HOME/course_completion_process
    if [ "$UNDEPLOY" = true ];
    then
        mvn jboss-as:undeploy
    else
        mvn jboss-as:deploy -DskipTests -Denvironment=dev
    fi
    if [ $? != 0 ];
    then
        echo "Failed course_completion_process (un)deploy." >&2;
        exit 1;
    fi
}

function deployUP() {
    echo -en "deployUP() \n\n"
    cd $PROJECT_HOME/gpte_universal_process
    if [ "$UNDEPLOY" = true ];
    then
        mvn jboss-as:undeploy
    else
        mvn jboss-as:deploy -DskipTests -Denvironment=dev
    fi
    if [ $? != 0 ];
    then
        echo "Failed gpte_uniersal_process deploy." >&2;
        exit 1;
    fi
}

function deploySReg() {
    echo -en "deploySReg() \n\n"
    cd $PROJECT_HOME/student_registration_process
    if [ "$UNDEPLOY" = true ];
    then
        mvn jboss-as:undeploy
    else
        mvn jboss-as:deploy -DskipTests -Denvironment=dev
    fi
    if [ $? != 0 ];
    then
        echo "Failed student_registration_process deploy." >&2;
        exit 1;
    fi
}

ensurePreReqs
deployCC
deployUP
deploySReg
