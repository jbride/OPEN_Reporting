package com.redhat.gpte.services;

public class InvalidStudentException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String INVALID_STUDENT_PREFIX = "\nInvalid student(s): ";
    public static final String VALIDATION_EXCEPTION_BUFFER = "validation_exception_buffer";

    public InvalidStudentException(){
        super(INVALID_STUDENT_PREFIX);
    }

    public InvalidStudentException(String message) {
        super(INVALID_STUDENT_PREFIX+message);
    }
    public InvalidStudentException(String message, Throwable cause) {
        super(INVALID_STUDENT_PREFIX+message, cause);
    }

}
