package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.exception.*;
import com.lucentblock.assignment2.security.exception.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put( toSnakeCase(((FieldError) c).getField()), c.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUserException(UserDuplicateException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("user_email", ex.getUsername());

        return ResponseEntity.status(HttpServletResponse.SC_CONFLICT).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "User Not Found");
        error.put("user_email", ex.getMessage()); // getUsername from exception object.

        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Password is incorrect");

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessTokenIsInvalid.class)
    public ResponseEntity<Map<String, String>> handleAccessTokenIsInvalid(AccessTokenIsInvalid ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Access Token is invalid");

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessTokenIsNotExpired.class)
    public ResponseEntity<Map<String, String>> handleAccessTokenIsNotExpired(AccessTokenIsNotExpired ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Access Token is not expired");

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenInvalidException(RefreshTokenInvalidException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Refresh Token is invalid");

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RefreshTokenDoesNotMatchException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenDoesNotMatchException(RefreshTokenDoesNotMatchException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Refresh Token does not match with Database");
        error.put("user_email", ex.getUsername());

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AlreadyVerifiedUserException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyVerifiedUserException(AlreadyVerifiedUserException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("user_email", ex.getUsername());

        return ResponseEntity.status(HttpServletResponse.SC_CONFLICT).body(error);
    }

    @ExceptionHandler(CodeDoesNotMatchException.class)
    public ResponseEntity<Map<String, String>> handleCodeDoesNotMatchException(CodeDoesNotMatchException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
    }


//////////////////////////////////////////////////////reserve////////////////////////////////////////////////////////
    @ExceptionHandler(ReserveTimeConflictException.class)
    public ResponseEntity<Map<String,String>> handleReserveTimeConflictException(ReserveTimeConflictException e){
        Map<String,String> error=new HashMap<>();

        error.put("message",e.getErrorCode().getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(error);
    }

    @ExceptionHandler(UnsatisfiedLicenseException.class)
    public ResponseEntity<Map<String,String>> handleUnsatisfiedLicenseException
            (UnsatisfiedLicenseException e){
        Map<String,String> error=new HashMap<>();

        error.put("message",e.getErrorCode().getMessage());
        error.put("repair_man_license",String.valueOf(e.getReserve().getRepairMan().getLicenseId()));
        error.put("required",String.valueOf(e.getReserve().getMaintenanceItem().getRequiredLicense()));

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(error);
    }

    @ExceptionHandler(ReservedWithNoMatchValueException.class)
    public ResponseEntity<Map<String,String>> handleReservedWithNoMatchValueException
            (ReservedWithNoMatchValueException e){

        Map<String,String> error=new HashMap<>();

        error.put("message",e.getErrorCode().getMessage());
        error.put("not_found_list",e.getErrorList().toString());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(error);
    }

    @ExceptionHandler(ReserveNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleReserveNotFoundException(ReserveNotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getErrorCode().getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(error);
    }
    //////////////////////////////////////////////////////car////////////////////////////////////////////////////////
    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<Map<String, String >> handleCarNotFoundException(CarNotFoundException ex) {
        Map<String, String> error = new HashMap<>();

        error.put("message", "Car Not Found");
        error.put("license_plate_no", ex.getMessage());

        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(error);
    }

    @ExceptionHandler(CarDuplicateException.class)
    public ResponseEntity<Map<String, String>> handleCarDuplicateException(CarDuplicateException ex) {
        Map<String, String> error = new HashMap<>();

        error.put("message", "There is a car with the same license plate number.");
        error.put("license_plate_no", ex.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_CONFLICT).body(error);
    }


    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleLocationNotFoundException(LocationNotFoundException e){
        Map<String, String> error = new HashMap<>();

        error.put("message", e.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(error);
    }

    @ExceptionHandler(RepairShopNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleLocationNotFoundException(RepairShopNotFoundException e){
        Map<String, String> error = new HashMap<>();

        error.put("message", e.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(error);
    }
}
