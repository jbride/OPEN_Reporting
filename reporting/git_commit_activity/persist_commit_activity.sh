#!/bin/bash

# Usage
#   ./persist_commit_activity.sh  -authToken=<github auth token>                                        :   execute all functions
#   ./persist_commit_activity.sh  -function=determineGitStatistics -authToken=<github auth token>       :   execute just determineGitStatistics function
#   ./persist_commit_activity.sh  -function=persistCommitActivity -authToken=<github auth token>        :   execute just persistCommitActivity function

# Import git_name_map
#   mysqlimport --fields-terminated-by=, --local --user=gpte_collection --password=gpte_collection gpte_collection git_name_map.txt

# Database queries
#   select SUM(a.commit_count), m.coninical_email from git_activity a, git_name_map m where a.contributor_email=m.git_email group by m.coninical_email order by SUM(a.commit_count);


for var in $@
do
    case $var in
        -authToken=*)
            authToken=`echo $var | cut -f2 -d\=`
            ;;
        -function=*)
            function=`echo $var | cut -f2 -d\=`
            ;;
    esac
done

ORG_ID=10223033                        # redhat-gpe
ORG_NAME=redhat-gpe
#ORG_ID=1629987                          # jboss-gpe
#ORG_NAME=jboss-gpe


OUTPUT=/tmp/persist_commit_activity.log
REPO_LIST_DIR=/tmp/repo_lists
NEXT_STRING="rel\=\"next\""
NAME_STRING="\"name\":"
CLONED_REPOS_DIR=/opt/cloned_redhat_repos
MYSQL_IMPORT_FILE=$CLONED_REPOS_DIR/git_activity.txt
MYSQL_USER_ID=gpte_collection
MYSQL_DB_NAME=gpte_collection
MYSQL_PASSWD=gpte_collection
SLEEP_DURATION=5
export GLOBAL_REPO_CLONED_COUNT=1

function persistCommitActivity {

    echo -en "persistCommitActivity() will persist the following # of records:  `cat $MYSQL_IMPORT_FILE | wc -l`\n" >>  $OUTPUT
    mysqlimport --fields-terminated-by=, --local --user=$MYSQL_USER_ID --password=$MYSQL_PASSWD $MYSQL_DB_NAME $MYSQL_IMPORT_FILE >> $OUTPUT
}

function determineGitStatistics {
    rm -f $MYSQL_IMPORT_FILE

    cd $CLONED_REPOS_DIR
    for repoDir in * ; do
        if [ -d "$repoDir" ]; then
            if [[ -s $repoDir/.git/FETCH_HEAD ]]; then
                cd $CLONED_REPOS_DIR/$repoDir
                GIT_LOOPS=0
                git shortlog -n -s -e | while read -r commitLine ; do
                    if [ $GIT_LOOPS -ne 0 ]; then
                        echo -en "\n" >> $MYSQL_IMPORT_FILE
                    fi

                    # 347\tJeffrey Bride <jbride@zareason.com>          # Example line; Notice the tab between commit count and full_name
                    commit_count=`echo "$commitLine" | cut -f1`         # get first token prior to tab
                    contributor_email=${commitLine#*<}                  # get token after `<`
                    contributor_email=${contributor_email%>*}           # prune `>`
                    echo "$repoDir,$commit_count,$contributor_email" >> $MYSQL_IMPORT_FILE
                done
                cd $CLONED_REPOS_DIR
            else
                echo -en "determineGitStatistics() $$ skipping the following unitialized repo: $repoDir\n" >> $OUTPUT
            fi
        fi
    done
}

function syncRepos {

    
    for repoListFile in $REPO_LIST_DIR/* ; do
        echo -en "syncRepos() $$ will clone repos found in:  $repoListFile\n" >>  $OUTPUT
        grep $NAME_STRING $repoListFile | while read -r line ; do
            repoName=${line:9} #Strip unnecessary beginning 14 characters
            repoName=${repoName%\"*} #String unnecessary end of string starting with double quote
            gitdir=$CLONED_REPOS_DIR/$repoName
            REPO_URL=git@$ORG_NAME.github.com:$ORG_NAME/$repoName
            echo -en "syncRepos() $$ $GLOBAL_REPO_CLONED_COUNT : will sync: $REPO_URL\n" >> $OUTPUT
            if [ ! -d $gitdir/.git ]
            then
                git clone $REPO_URL $gitdir
                if [ $? -ne 0 ]; then
                    echo "syncRepos: problem cloning the following repo: $REPO_URL"; >> $OUTPUT
                    exit 1;
                fi
            else
                cd $gitdir
                git fetch >> $OUTPUT
                if [ $? -ne 0 ]; then
                    echo "syncRepos: problem fetching to the following local repo: `pwd`" >> $OUTPUT
                    exit 1;
                fi
            fi
            ((GLOBAL_REPO_CLONED_COUNT++))
            sleep $SLEEP_DURATION
        done
    done
}

function getRepoLists {
    rm -f $REPO_LIST_DIR
    mkdir $REPO_LIST_DIR

    LOOPS=1
    while true; do
        REPO_OUTPUT=$REPO_LIST_DIR/repos$LOOPS.json
        URL=https://api.github.com/organizations/$ORG_ID/repos?page=$LOOPS
        echo -en "\ngetRepoLists() $$ URL = $URL \tOUTPUT = $REPO_OUTPUT\n" >>  $OUTPUT
        curl -i -H "Authorization: token $authToken" $URL > $REPO_OUTPUT
        echo -en "getRepoLists() $$ # of repos found in $REPO_OUTPUT = `grep $NAME_STRING $REPO_OUTPUT | wc -l`\n" >> $OUTPUT

        # Break out of loop if existing response does next include a next link
        if grep  $NEXT_STRING $REPO_OUTPUT
        then
            ((LOOPS++))
        else
            break;
        fi
    done
}

function validateAndInit {
    if [ "x$authToken" = "x" ]; then
        echo -en "\nERROR:  Need to invoke this script with the following parameter: -authToken=\n\n"
        exit 1;
    fi


    echo $$ `date` > $OUTPUT
    if [ "x$function" = "x" ]; then
        getRepoLists
        syncRepos
        determineGitStatistics
        persistCommitActivity
    elif [ "$function" = "determineGitStatistics" ]; then
        determineGitStatistics
    elif [ "$function" = "persistCommitActivity" ]; then
        persistCommitActivity
    fi
}
    

validateAndInit
