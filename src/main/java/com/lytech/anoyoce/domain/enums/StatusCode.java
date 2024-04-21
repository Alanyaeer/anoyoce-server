package com.lytech.anoyoce.domain.enums;

public enum  StatusCode {
    SUCCESS(200),
    ERROR(500);
    private final int code;
    StatusCode(int code){
        this.code = code;
    }
    public int getCodeVal(){
        return this.code;
    }
}
