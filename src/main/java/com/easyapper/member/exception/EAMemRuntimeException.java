package com.easyapper.member.exception;

public class EAMemRuntimeException extends RuntimeException{
    public final int code;
    public final String message;

    public EAMemRuntimeException(ErrorCode code){
        super();
        this.code = code.val();
        this.message = "";
    }

    public EAMemRuntimeException(ErrorCode code, String msg){
        super(msg);
        this.code = code.val();
        this.message = msg;
    }
}
