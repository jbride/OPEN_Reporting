package com.redhat.gpte.studentregistration.util;

public class InvalidAttributeException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String INVALID_ATTRIBUTE_PREFIX = "Invalid student attribute(s): ";
    public static final String VALIDATION_EXCEPTION_BUFFER = "validation_exception_buffer";

    private String assessFile;
    
    public InvalidAttributeException(){
        super(INVALID_ATTRIBUTE_PREFIX);
    }

    public InvalidAttributeException(String message, String assessFile) {
        super(INVALID_ATTRIBUTE_PREFIX+message);
        this.assessFile = assessFile;
    }
    public InvalidAttributeException(String message) {
        super(INVALID_ATTRIBUTE_PREFIX+message);
    }
    public InvalidAttributeException(String message, Throwable cause) {
        super(INVALID_ATTRIBUTE_PREFIX+message, cause);
    }

    public String getAssessFile() {
        return assessFile;
    }

}
