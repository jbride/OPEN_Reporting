package com.redhat.gpe.accreditation.service;

public class SkillsBaseCommunicationException extends Exception {

    private static final long serialVersionUID = 1L;

    private String assessFile;
    
    public SkillsBaseCommunicationException(){
        super();
    }

    public SkillsBaseCommunicationException(String message) {
        super(message);
    }
    public SkillsBaseCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
