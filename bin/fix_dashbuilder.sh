#!/bin/bash

systemctl stop dashbuilder
touch /opt/jboss/wildfly-8.2.0.Final/standalone/deployments/dashbuilder.war.dodeploy
systemctl start dashbuilder
