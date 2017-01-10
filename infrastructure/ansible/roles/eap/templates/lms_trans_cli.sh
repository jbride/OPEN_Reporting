if (outcome != success) of /subsystem=datasources/jdbc-driver=mysql:read-attribute(name=driver-name)
  /subsystem=datasources/jdbc-driver=mysql:add(driver-xa-datasource-class-name=com.mysql.jdbc.jbdc2.optional.MysqlXAConnection,driver-name=mysql,driver-module-name=com.mysql)
end-if

if (outcome != success) of /subsystem=datasources/data-source=transactionalDS:read-attribute(name=enabled)
  # transactionalDS
  data-source add --name=transactionalDS --jndi-name="java:jboss/datasources/transactionalDS" --driver-name=mysql --user-name={{lms_trans_userId}} --password={{lms_trans_passwd}} --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker --connection-url="jdbc:mysql://${mysql.host.ip}:${mysql.host.port}/${lms_trans_db_name}?transformedBitIsBoolean=true&sessionVariables=storage_engine=InnoDB"
  /subsystem=datasources/data-source=transactionalDS:enable

end-if
