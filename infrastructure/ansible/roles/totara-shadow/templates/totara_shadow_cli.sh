# Create JBoss EAP module based on jdbc driver library installed from RHN
module add --name={{totara_shadow_db_module_name}} --resources={{totara_shadow_db_driver_jar_dir}}/{{totara_shadow_db_driver_jar}} --dependencies=javax.api,javax.transaction.api

if (outcome != success) of /subsystem=datasources/jdbc-driver=postgresql:read-attribute(name=driver-name)
  /subsystem=datasources/jdbc-driver=postgresql:add(driver-xa-datasource-class-name=org.postgresql.xa.PGXAConnection,driver-name=postgresql,driver-module-name=org.postgresql)
end-if

if (outcome != success) of /subsystem=datasources/data-source=totaraShadowDS:read-attribute(name=enabled)
  # totaraShadowDS
  data-source add --name=totaraShadowDS --jndi-name="java:jboss/datasources/totaraShadowDS" --driver-name=postgresql --user-name={{totara_shadow_db_username}} --password={{totara_shadow_db_password}} --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker --connection-url="jdbc:postgresql://{{totara.shadow.host.ip}}:{{totara.shadow.host.port}}/{{totara.shadow.db.name}}"
  /subsystem=datasources/data-source=totaraShadowDS:enable

end-if
