#!/bin/sh

export PGPASSWORD={{ utilization_db_password }}

echo -en "PGPASSWORD=$PGPASSWORD\n"

echo -en "pull utilization data: $1\n"
psql -h {{ utilization_db_host }} -U {{ utilization_db_username  }} -w $1 -c "\copy provisioned_services TO '{{database_update_script_dir}}/$1-db.csv' CSV DELIMITER ','"

echo -en "load utilization data: $1\n"
mysql -u root {{lms_reporting_db_name}} -e "truncate table lms_reporting.$1;"
mysql -u root {{lms_reporting_db_name}} -e "load data local infile '{{database_update_script_dir}}/$1-db.csv' into table lms_reporting.$1 fields terminated by ',' lines terminated by '\n';"
