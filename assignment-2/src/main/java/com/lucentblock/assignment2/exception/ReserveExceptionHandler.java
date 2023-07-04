package com.lucentblock.assignment2.exception;


import com.lucentblock.assignment2.security.exception.UserDuplicateException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Array;
import java.util.*;

@RestControllerAdvice
@Slf4j
public class ReserveExceptionHandler extends ResponseEntityExceptionHandler { // Spring 내부 예외 처리를 위한 상속

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
        List<String> list=new ArrayList<>();

        if(e.getSet().getCar()==null) list.add("car");
        if(e.getSet().getRepairShop()==null) list.add("repair_shop");
        if(e.getSet().getRepairMan()==null) list.add("repair_man");
        if(e.getSet().getMaintenanceItem()==null) list.add("maintenance_item");

        error.put("message",e.getErrorCode().getMessage());
        error.put("not_found_list",list.toString());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(error);
    }

//    @ExceptionHandler(ReservedWithNullValueException.class)
//    public ResponseEntity<Map<String,String>> handleReservedWithNullValueException
//            (ReservedWithNullValueException e){
//
//        Map<String,String> error=new HashMap<>();
//        list.add();
//
//        error.put("message",e.getErrorCode().getMessage());
//
//
//        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(error);
//    }
}
