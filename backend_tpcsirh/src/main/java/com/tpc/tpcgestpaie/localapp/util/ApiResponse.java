package com.tpc.tpcgestpaie.localapp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private boolean isExpiredToken;
    private String errorCode;
    private Map<String, String> errors;

    public ApiResponse() {}

    public ApiResponse(
            boolean success,
            String message,
            T data,
            boolean isExpiredToken,
            String errorCode,
            Map<String, String> errors
    ) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.isExpiredToken = isExpiredToken;
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, false, null, null);
    }

    // =======================
    // Helpers SUCCESS
    // =======================

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // =======================
    // Helpers ERROR (SURCHARGES)
    // =======================

    /** 🔹 Erreurs sous forme Map (field -> message) */
    public static <T> ApiResponse<T> error(
            String message,
            String errorCode,
            Map<String, String> errors
    ) {
        return new ApiResponse<>(false, message, null, false, errorCode, errors);
    }

/*    public static <T> ApiResponse<T> error(String message, String errorCode, List<String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setErrors(errors);
        return response;
    }*/

  /*  public static <T> ApiResponse<T> error(String message) {
        return error(message, null, null);
    }*/
    /** 🔹 Erreur simple avec message et code */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, false, errorCode, null);
    }

    /** 🔹 Erreur avec message, code et data (utile si on veut renvoyer un objet lié à l'erreur) */
    public static <T> ApiResponse<T> error(String message, String errorCode, T data) {
        return new ApiResponse<>(false, message, data, false, errorCode, null);
    }

    /** 🔹 Erreur avec message, code et exception (pour logging / debug) */
    public static <T> ApiResponse<T> error(String message, String errorCode, Throwable ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("exception", ex.getClass().getSimpleName());
        errorMap.put("message", ex.getMessage());
        return new ApiResponse<>(false, message, null, false, errorCode, errorMap);
    }

    /** 🔹 Erreurs sous forme de liste (compatibilité controllers existants) */
    public static <T> ApiResponse<T> error(
            String message,
            String errorCode,
            List<String> errors
    ) {
        Map<String, String> mappedErrors = new HashMap<>();
        for (int i = 0; i < errors.size(); i++) {
            mappedErrors.put("error_" + (i + 1), errors.get(i));
        }
        return new ApiResponse<>(false, message, null, false, errorCode, mappedErrors);
    }

    /** 🔹 Erreur simple sans détails */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, false, "ERR_GENERIC", null);
    }

    /** 🔹 Token expiré */
    public static <T> ApiResponse<T> expiredToken() {
        return new ApiResponse<>(
                false,
                "Token expiré",
                null,
                true,
                "TOKEN_EXPIRED",
                null
        );
    }

    // =======================
    // Getters & Setters
    // =======================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isExpiredToken() {
        return isExpiredToken;
    }

    public void setExpiredToken(boolean expiredToken) {
        isExpiredToken = expiredToken;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
