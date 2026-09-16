package com.tpc.tpcgestpaie.localapp.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestion de l'exception liée au token JWT expiré.
     * Renvoie un 401 Unauthorized avec un message adapté.
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(ExpiredJwtException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "Le token est expiré. Veuillez vous reconnecter.",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gestion des erreurs de parsing JSON dans la requête HTTP.
     * Renvoie un 400 Bad Request avec le détail de l'erreur JSON.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();

        Map<String, Object> details = new HashMap<>();

        if (cause instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException) cause;
            details.put("errorType", "UnrecognizedField");
            details.put("field", upe.getPropertyName());
            details.put("class", upe.getReferringClass().getSimpleName());
            details.put("knownProperties", upe.getKnownPropertyIds());

            JsonLocation loc = upe.getLocation();
            if (loc != null) {
                Map<String, Object> location = new HashMap<>();
                location.put("line", loc.getLineNr());
                location.put("column", loc.getColumnNr());
                details.put("location", location);
            }
        } else {
            details.put("error", cause.getMessage());
        }

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "Erreur dans le format JSON de la requête.",
                details
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestion des erreurs de validation des paramètres d'entrée (@Valid).
     * Renvoie un 400 Bad Request avec la liste des erreurs par champ.
     */
   /* @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false,
                "Erreurs de validation",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
*/
    // Vous pouvez ajouter ici d'autres @ExceptionHandler pour d'autres erreurs

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Gère aussi les autres exceptions si tu veux
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Erreur serveur interne", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Erreur de validation", null,false,"400",errors));
    }

}
