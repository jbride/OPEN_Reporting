# Create JBoss EAP module based on jdbc driver library installed from RHN
module add --name=@@lms_reporting_module_name@@ --resources=@@lms_reporting_driver_jar_dir@@/@@lms_reporting_driver_jar@@ --dependencies=javax.api,javax.transaction.api
