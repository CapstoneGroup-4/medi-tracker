package edu.capstone4.userserver.exception;

public enum ErrorCode {
    USER_INACTIVE(10001, "Error: User is not active!"),
    USER_NAME_EXIST(10002, "Error: Username is already taken!"),
    USER_EMAIL_EXIST(10003, "Error: Email is already in use!"),
    CODE_VERIFY_FAILED(10004, "Error: Verification code verify failed!"),
    DOCTOR_EXIST(10005, "Error: Doctor already exist!"),
    USER_NOT_FOUND(10006, "Error: User not found!"),
    ROLE_NOT_FOUND(10007, "Error: Role is not found."),
    ROLE_CANNOT_SET_ADMIN(10008, "Error: Cannot set role to admin."),
    DOCTOR_NOT_FOUND(10009, "Error: Doctor not found!"),
    DOCTOR_ALREADY_ACTIVATED(10010, "Error: Doctor is already activated."),
    ;


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}


