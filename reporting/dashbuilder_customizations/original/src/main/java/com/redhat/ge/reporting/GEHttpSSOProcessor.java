package com.redhat.ge.reporting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.ui.controller.requestChain.HttpSSOProcessor;
import org.jboss.dashboard.users.Role;
import org.jboss.dashboard.users.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.security.SecurityContext;  
import org.jboss.security.SecurityContextAssociation;  
import org.jboss.security.identity.RoleGroup;
import org.picketbox.util.PicketBoxUtil;  
 
/* Purpose:
 *   1) Define root sys admin role for Dashbuilder ( cn=forge-users,cn=groups,cn=accounts,dc=opentlc,dc=com )
 *   2) For privledged roles, dictate the seniority of those roles 
 *      - Otherwise, if a user account has been assigned mulitple privledges roles, then Dashbuilder will utilize those roles in a non-deterministic manner
 */ 

@ApplicationScoped
public class GEHttpSSOProcessor extends HttpSSOProcessor {
    
    public static final String GE_IPA_PREFIX="cn=";
    public static final String GE_IPA_PARTNER_PREFIX="cn=rhpds-partner-";
    public static final String GE_IPA_GEO_PREFIX="cn=rhpds-geo-";
    public static final String GE_IPA_MEMBER_OF_SUFFIX=",cn=groups,cn=accounts,dc=opentlc,dc=com";

    public static final String GE_IPA_FORGE_USERS="cn=forge-users,cn=groups,cn=accounts,dc=opentlc,dc=com";
    public static final String GE_IPA_REPORT_CREATORS="cn=report-creators,cn=groups,cn=accounts,dc=opentlc,dc=com";
    public static final String GE_IPA_POWER_REPORTING_USERS="cn=power-reporting-users,cn=groups,cn=accounts,dc=opentlc,dc=com";
    public static final String GE_IPA_OPEN_REPORTING_USERS="cn=open-reporting-users,cn=groups,cn=accounts,dc=opentlc,dc=com";
    public static final String GE_IPA_RHSE_REPORTING_USERS="cn=rhse-reporting-users,cn=groups,cn=accounts,dc=opentlc,dc=com";
    public static final String GE_IPA_PARTNER_USERS="cn=partner-users,cn=groups,cn=accounts,dc=opentlc,dc=com";

    Logger log = null;
    
    @PostConstruct
    public void init() {
        log = LoggerFactory.getLogger(GEHttpSSOProcessor.class);
        log.debug("init()");
    }

    private void initSession(String login, GEUserStatus us, String role) {
        log.debug("Setting UserStatus role to "+role);
        Set<String> roleIds = new HashSet<String>();                
        roleIds.add(role);
        us.initSession(login, roleIds);
    }
    
    public boolean processRequest() throws Exception {
        HttpServletRequest request = getHttpRequest();
        String login = request.getRemoteUser();
        GEUserStatus us = (GEUserStatus)UserStatus.lookup();

        if (!StringUtils.isBlank(login) && us.isAnonymous()) {
            log.debug("processRequest() configured root user = "+us.getRootLogin()+" : this remote user = "+login);
        
            // Dictate the seniority of priviledged roles    
            if (us.getRootLogin().equals(login) || (request.isUserInRole(GE_IPA_FORGE_USERS))) {
                // Login as root since user's login = value of this sys property:  org.jboss.dashboard.users.UserStatus.rootLogin
                log.debug("processRequest() logging in as root: "+login);
                us.initSessionAsRoot();
            } else if (request.isUserInRole(GE_IPA_REPORT_CREATORS)) {
                initSession(login, us, GE_IPA_REPORT_CREATORS);
            } else if (request.isUserInRole(GE_IPA_POWER_REPORTING_USERS)) {
                initSession(login, us, GE_IPA_POWER_REPORTING_USERS);
            } else if (request.isUserInRole(GE_IPA_OPEN_REPORTING_USERS)) {
                initSession(login, us, GE_IPA_OPEN_REPORTING_USERS);
            } else if (request.isUserInRole(GE_IPA_RHSE_REPORTING_USERS)) {
                initSession(login, us, GE_IPA_RHSE_REPORTING_USERS);
            } else if (request.isUserInRole(GE_IPA_PARTNER_USERS)) {
                initSession(login, us, GE_IPA_PARTNER_USERS);

                // Since this is a user with no RHT priledges, add partner and geo role to GEUserStatus
                // "partner" and "geo" roles will subsequently be used to filter data queried data
                SecurityContext sc = SecurityContextAssociation.getSecurityContext();  
                RoleGroup rG = PicketBoxUtil.getRolesFromSubject(sc.getUtil().getSubject()); 
                List<org.jboss.security.identity.Role> sessionRoles = rG.getRoles();
                for(org.jboss.security.identity.Role sessionRole : sessionRoles) {
                       String sessionRoleId = sessionRole.getRoleName();
                    if (sessionRoleId.startsWith(GE_IPA_PARTNER_PREFIX)) {
                        sessionRoleId = sessionRoleId.substring(3, sessionRoleId.indexOf(GE_IPA_MEMBER_OF_SUFFIX));
                        us.setPartnerId(sessionRoleId);
                        log.debug("Adding the following partner to GEUserStatus: "+sessionRoleId);
                    }else if (sessionRoleId.startsWith(GE_IPA_GEO_PREFIX)) {
                        sessionRoleId = sessionRoleId.substring(3, sessionRoleId.indexOf(GE_IPA_MEMBER_OF_SUFFIX));
                        us.setGeo(sessionRoleId);
                        log.debug("Adding the following geo to GEUserStatus: "+sessionRoleId);
                    }
                }
            } else {
                throw new RuntimeException("processRequest() no valid roles found for user: "+login);
            }

        }
        return true;
    }

}
