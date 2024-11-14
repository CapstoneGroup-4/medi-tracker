package edu.capstone4.userserver.exceptions;

import edu.capstone4.userserver.payload.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException ex) {
        BaseResponse<Void> response = new BaseResponse<>(ex.getMessage(), ex.getErrorCode().getCode());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception ex) {
        BaseResponse<Void> response = new BaseResponse<>("Internal server error", 500);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleRoleNotFoundException(RoleNotFoundException ex) {
        BaseResponse<Void> response = new BaseResponse<>(ex.getMessage(), ex.getErrorCode().getCode());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}


