package com.tpc.tpcgestpaie.localapp.util;

public class ErrorResponse {
    private String field;
    private String message;

    // Constructeur par défaut requis par Jackson
    public ErrorResponse() {
    }

    public ErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
