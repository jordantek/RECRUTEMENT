package com.tpc.tpcgestpaie.localapp.exception.conge;

public class SoldeInsuffisantException extends RuntimeException {

    public SoldeInsuffisantException(String message) {
        super(message);
    }

    public SoldeInsuffisantException(String message, Throwable cause) {
        super(message, cause);
    }
}