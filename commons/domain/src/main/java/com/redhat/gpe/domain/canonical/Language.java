/**
 * This class is generated by jOOQ
 */
package com.redhat.gpe.domain.canonical;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Language implements Serializable {

    private static final long serialVersionUID = 2036507555;

    public static final String LANGUAGE_ID = "languageid";
    public static final String LANGUAGE_NAME = "languagename";

    public static final String FROM_CLAUSE = "l.languageid,l.languagename";

    private String languageid;
    private String languagename;

    public Language() {}

    public Language(Language value) {
        this.languageid = value.languageid;
        this.languagename = value.languagename;
    }

    public Language(
        String languageid,
        String languagename
    ) {
        this.languageid = languageid;
        this.languagename = languagename;
    }

    public String getLanguageid() {
        return this.languageid;
    }

    public void setLanguageid(String languageid) {
        this.languageid = languageid;
    }

    public String getLanguagename() {
        return this.languagename;
    }

    public void setLanguagename(String languagename) {
        this.languagename = languagename;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Languages (");

        sb.append(languageid);
        sb.append(", ").append(languagename);

        sb.append(")");
        return sb.toString();
    }
}
