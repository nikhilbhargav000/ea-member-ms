package com.easyapper.member.exception;

public class MemberRuntimeException extends RuntimeException{
    public final int code;
    public final String message;

    public MemberRuntimeException(ErrorCode code){
        super();
        this.code = code.val();
        this.message = "";
    }

    public MemberRuntimeException(ErrorCode code, String msg){
        super(msg);
        this.code = code.val();
        this.message = msg;
    }
}
