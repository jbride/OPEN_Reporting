package com.redhat.gpte.services;

public class AttachmentValidationException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String VALIDATION_EXCEPTION_BUFFER = "validation_exception_buffer";
    public static final String VALIDATION_EXCEPTION_PREFIX="*** ATTACHMENT VALIDATION ERROR: ";
    public static final String ATTACHMENT_MUST_HAVE_CSV_SUFFIX = "Attachment must have a suffix of csv (or tsv if file is tab separated): ";
    public static final String NO_ATTACHMENTS_PROVIDED = "No attachments provided: ";
    public static final String INVALID_NUM_ELEMENTS = "Invalid number of elements";

    private String assessFile;
    
    public AttachmentValidationException(){
        super(VALIDATION_EXCEPTION_PREFIX);
    }

    public AttachmentValidationException(String message, String assessFile) {
        super(VALIDATION_EXCEPTION_PREFIX+message);
        this.assessFile = assessFile;
    }
    public AttachmentValidationException(String message) {
        super(VALIDATION_EXCEPTION_PREFIX+message);
    }
    public AttachmentValidationException(String message, Throwable cause) {
        super(VALIDATION_EXCEPTION_PREFIX+message, cause);
    }

    public String getAssessFile() {
        return assessFile;
    }

}
