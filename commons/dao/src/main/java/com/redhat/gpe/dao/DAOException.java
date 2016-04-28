package com.redhat.gpe.dao;

public class DAOException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DAOException() {
        super();
    }

    public DAOException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DAOException(String arg0) {
        super(arg0);
    }

    public DAOException(Throwable arg0) {
        super(arg0);
    }    
}
