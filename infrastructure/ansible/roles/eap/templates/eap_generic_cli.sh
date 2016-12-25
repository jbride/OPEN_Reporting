# Create JBoss EAP module based on jdbc driver library installed from RHN
module add --name=@@lms_trans_module_name@@ --resources=@@lms_trans_driver_jar_dir@@/@@lms_trans_driver_jar@@ --dependencies=javax.api,javax.transaction.api

batch

# file logger should refresh after every bounce of server
/subsystem=logging/periodic-rotating-file-handler=FILE:write-attribute(name=append,value=false)

run-batch
