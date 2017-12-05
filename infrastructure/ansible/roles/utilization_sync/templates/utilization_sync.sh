#!/bin/sh

export PGPASSWORD={{ utilization_db_password }}

echo -en "utilization_sync" > $2
echo -en "PGPASSWORD=$PGPASSWORD\n" >> $2

echo -en "pull utilization data: $1\n" >> $2
psql -h {{ utilization_db_host }} -U {{ utilization_db_username  }} -w $1 -c "\copy provisioned_services TO '{{database_update_script_dir}}/$1-db.csv' CSV DELIMITER ','"
if [ $? != 0 ];
then
    exit 1;
fi

echo -en "load utilization data: $1\n" >> $2
mysql -u root {{lms_reporting_db_name}} -e "truncate table lms_reporting.$1;"
if [ $? != 0 ];
then
    exit 1;
fi

mysql -u root {{lms_reporting_db_name}} -e "load data local infile '{{database_update_script_dir}}/$1-db.csv' into table lms_reporting.$1 fields terminated by ',' lines terminated by '\n';"
if [ $? != 0 ];
then
    exit 1;
fi

echo -en "run procedure to update dashbuilder" >> $2
mysql -u root {{lms_reporting_db_name}} -e "call refresh_labs_demos;"
if [ $? != 0 ];
then
    exit 1;
fi
