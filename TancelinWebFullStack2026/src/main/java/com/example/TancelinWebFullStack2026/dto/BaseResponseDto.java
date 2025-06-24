package com.example.TancelinWebFullStack2026.dto;

import org.springframework.validation.ObjectError;

public class BaseResponseDto {
    private Object message;
    private Object data;

    public BaseResponseDto(Object message, Object data) {
        this.message = message;
        this.data = data;
    }

    public BaseResponseDto(Object message) {
        this.message = message;

    }
    public BaseResponseDto() {

    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponseDto{" +
                "message=" + message +
                ", data=" + data +
                '}';
    }
}
