package edu.capstone4.userserver.exception;

public class RoleNotFoundException extends RuntimeException {
    private ErrorCode errorCode;

    public RoleNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
