package com.lytech.anoyoce.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lytech.anoyoce.domain.enums.StatusCode;

import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public  class ResponseResult<T> {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示信息，如果有错误时，前端可以获取该字段进行提示
     */
    private String msg;
    /**
     * 查询到的结果数据，
     */
    private T data;

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }
    public ResponseResult() {

    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static <T> ResponseResult<T> success(T data){
//        ResponseResult.ResponseResultBuilder
        ResponseResult<T> responseResult = (ResponseResult<T>) ResponseResult.builder()
                .code(StatusCode.SUCCESS.getCodeVal())
                .data(data)
                .msg("请求成功").build();
        return responseResult;
    }
    public static <T> ResponseResult<T> error(T data){
        ResponseResult<T> responseResult = (ResponseResult<T>) ResponseResult.builder()
                .code(StatusCode.ERROR.getCodeVal())
                .data(data)
                .msg("请求失败").build();
        return responseResult;
    }
}
