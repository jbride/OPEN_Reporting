batch

  module add --name=@@lms_trans_module_name@@ --resources=@@lms_trans_driver_jar_dir@@/@@lms_trans_driver_jar@@ --dependencies=javax.api,javax.transaction.api

run-batch
