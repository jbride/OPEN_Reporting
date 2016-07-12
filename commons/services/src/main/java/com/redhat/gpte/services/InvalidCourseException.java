package com.redhat.gpte.services;

public class InvalidCourseException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String INVALID_COURSE_PREFIX = "\nInvalid course(s): ";
    public static final String VALIDATION_EXCEPTION_BUFFER = "validation_exception_buffer";

    private String assessFile;
    
    public InvalidCourseException(){
        super(INVALID_COURSE_PREFIX);
    }

    public InvalidCourseException(String message, String assessFile) {
        super(INVALID_COURSE_PREFIX+message);
        this.assessFile = assessFile;
    }
    public InvalidCourseException(String message) {
        super(INVALID_COURSE_PREFIX+message);
    }
    public InvalidCourseException(String message, Throwable cause) {
        super(INVALID_COURSE_PREFIX+message, cause);
    }

    public String getAssessFile() {
        return assessFile;
    }

}
