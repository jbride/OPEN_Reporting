package com.redhat.ge.reporting;

import java.util.Set;

import org.jboss.dashboard.command.AbstractCommand;
import org.jboss.dashboard.ui.NavigationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GELoggedUserCommand extends AbstractCommand {
    
    public static final String LOGGED_USER_PARTNER = "logged_user_partner";
    public static final String LOGGED_USER_GEO = "logged_user_geo";
    public static final String ORGANIZATION_FIELD = "s.company";
    public static final String GEO_FIELD = "s.region";
    public static final String EQUALS = "=\"";
    
    public static Logger log = LoggerFactory.getLogger(GELoggedUserCommand.class);
    
    public GELoggedUserCommand(String commandName) {
        super(commandName);
    }

    public String execute() throws Exception {
        NavigationManager navMgr = NavigationManager.lookup();
        GEUserStatus userCtx = (GEUserStatus)navMgr.getUserStatus();
        String commandName = getName();
        log.debug("execute() commandName = "+commandName);

        if (LOGGED_USER_PARTNER.equals(commandName)) {
            // The following data provider triggers this code block:
                // select s.email, s.region, s.subregion, s.country, s.partnertype, sa.accreditation, sa.enddate from Students s, StudentAccreditations sa where s.email=sa.email and { logged_user_partner, COMPANY};
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(this.ORGANIZATION_FIELD);
            sBuilder.append(this.EQUALS);
            sBuilder.append(userCtx.getPartnerId());
            sBuilder.append("\"");
            return sBuilder.toString();
        }
        if (LOGGED_USER_GEO.equals(commandName)) {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(this.GEO_FIELD);
            sBuilder.append(this.EQUALS);
            sBuilder.append(userCtx.getGeo());
            sBuilder.append("\"");
            return sBuilder.toString();
        }
        return null;
    }

}
