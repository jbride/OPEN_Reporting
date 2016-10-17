/**
 * This class is generated by jOOQ
 */
package com.redhat.gpe.domain.canonical;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccreditationDefinition implements Serializable {

    private static final long serialVersionUID = -1476098812;
    public static final String ACCREDITATION_ID = "AccreditationID";
    public static final String ACCREDITATION_NAME = "AccreditationName";
    public static final String ROLE = "Role";
    public static final String SPECIALIZATION = "Specialization";
    public static final String TRACK = "Track";
    public static final String PROFICIENCY = "Proficiency";
    public static final String ACCREDITATION_EXPORT_ID = "AccreditationExportID";
    public static final String SELECT_CLAUSE = "a.AccreditationID,a.AccreditationName,a.Role,a.Specialization,a.Track,a.Proficiency,a.AccreditationExportID";

    private Integer accreditationid = 0;
    private String  accreditationname;
    private String  role;
    private String  specialization;
    private String  track;
    private String  proficiency;
    private String  accreditationexportid;

    public AccreditationDefinition() {}

    public AccreditationDefinition(AccreditationDefinition value) {
        this.accreditationid = value.accreditationid;
        this.accreditationname = value.accreditationname;
        this.role = value.role;
        this.specialization = value.specialization;
        this.track = value.track;
        this.proficiency = value.proficiency;
        this.accreditationexportid = value.accreditationexportid;
    }

    public AccreditationDefinition(
        Integer accreditationid,
        String  accreditationname,
        String  role,
        String  specialization,
        String  track,
        String  proficiency,
        String  accreditationexportid
    ) {
        this.accreditationid = accreditationid;
        this.accreditationname = accreditationname;
        this.role = role;
        this.specialization = specialization;
        this.track = track;
        this.proficiency = proficiency;
        this.accreditationexportid = accreditationexportid;
    }

    public Integer getAccreditationid() {
        return this.accreditationid;
    }

    public void setAccreditationid(Integer accreditationid) {
        this.accreditationid = accreditationid;
    }

    public String getAccreditationname() {
        return this.accreditationname;
    }

    public void setAccreditationname(String accreditationname) {
        this.accreditationname = accreditationname;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSpecialization() {
        return this.specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getTrack() {
        return this.track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getProficiency() {
        return this.proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public String getAccreditationexportid() {
        return this.accreditationexportid;
    }

    public void setAccreditationexportid(String accreditationexportid) {
        this.accreditationexportid = accreditationexportid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Accreditations (");

        sb.append(accreditationid);
        sb.append(", ").append(accreditationname);
        sb.append(", ").append(role);
        sb.append(", ").append(specialization);
        sb.append(", ").append(track);
        sb.append(", ").append(proficiency);
        sb.append(", ").append(accreditationexportid);

        sb.append(")");
        return sb.toString();
    }
}
