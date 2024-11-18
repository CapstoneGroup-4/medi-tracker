package edu.capstone4.userserver.exceptions;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "Error: Internal server error!"),
    USER_INACTIVE(10001, "Error: User is not active!"),
    USER_NAME_EXIST(10002, "Error: Username is already taken!"),
    USER_EMAIL_EXIST(10003, "Error: Email is already in use!"),
    CODE_VERIFY_FAILED(10004, "Error: Verification code verify failed!"),
    DOCTOR_EXIST(10005, "Error: Doctor already exist!"),
    USER_NOT_FOUND(10006, "Error: User not found!"),
    ROLE_NOT_FOUND(10007, "Error: Role is not found."),
    DOCTOR_NOT_FOUND(10008, "Error: Doctor not found!"),
    ROLE_CANNOT_SET_ADMIN(10009, "Error: Cannot set role to admin."),
    FAILED_TO_CREATE_MEDICAL_RECORD(10010, "Error: Failed to create medical record."),
    MEDICAL_RECORD_NOT_FOUND(10011, "Error: Medical record not found."),
    UNAUTHORIZED_ACCESS(10012, "Error: Unauthorized access!"),
    RECORD_ACCESS_REVOCATION_FAILED(10013, "Error: Failed to revoke access to the record."),
    DOCTOR_ALREADY_ACTIVATED(10014, "Error: Doctor is already activated."),
    RECORD_NOT_FOUND(10015, "Error: Record not found."),
    ATTACHMENT_NOT_FOUND(10016, "Error: Attachment not found."),
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


