# Create JBoss EAP module based on jdbc driver library installed from RHN
module add --name={{lms_reporting_module_name}} --resources={{lms_reporting_driver_jar_dir}}/{{lms_reporting_driver_jar}} --dependencies=javax.api,javax.transaction.api

batch

/subsystem=logging/logger=com.redhat.ge.reporting/:add(category=com.redhat.ge.reporting,level=DEBUG)
/subsystem=logging/logger=org.jboss.security.auth.spi.AbstractServerLoginModule/:add(category=org.jboss.security.auth.spi.AbstractServerLoginModule,level=TRACE)
/subsystem=logging/logger=org.jboss.security/:add(category=org.jboss.security,level=INFO)


run-batch
