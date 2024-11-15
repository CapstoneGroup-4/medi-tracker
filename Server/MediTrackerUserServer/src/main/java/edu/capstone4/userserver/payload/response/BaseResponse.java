package edu.capstone4.userserver.payload.response;

public class BaseResponse<T> {
    private String message;
    private T data;
    private int code = 0;

    public BaseResponse(String message) {
        this.message = message;
    }

    public BaseResponse(T data) {
        this.data = data;
    }

    public BaseResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public BaseResponse(String message, T data, int code) {
        this.message = message;
        this.data = data;
        this.code = code;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                '}';
    }
}
