package com.redhat.ge.reporting;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.dashboard.command.Command;
import org.jboss.dashboard.command.DashboardCommandFactory;

@ApplicationScoped
public class GEDashbuilderCommandFactory extends DashboardCommandFactory {
	
	public Command createCommand(String commandName) {
		Command cObj = super.createCommand(commandName);
		if(cObj == null){
	        if (commandName.equals(GELoggedUserCommand.LOGGED_USER_PARTNER)) return new GELoggedUserCommand(GELoggedUserCommand.LOGGED_USER_PARTNER);
	        if (commandName.equals(GELoggedUserCommand.LOGGED_USER_GEO)) return new GELoggedUserCommand(GELoggedUserCommand.LOGGED_USER_GEO);
		}
	    return cObj;
	}

}
