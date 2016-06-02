package com.redhat.ge.reporting;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.jboss.dashboard.users.LogoutSurvivor;
import org.jboss.dashboard.users.UserStatus;

@SessionScoped
@Named("userStatus")
public class GEUserStatus extends UserStatus implements LogoutSurvivor, Serializable {
	
	private String partnerId = null;
	private String geo = null;
	
	public GEUserStatus() {
		super();
	}
	public void clear() {
		super.clear();
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}

}
