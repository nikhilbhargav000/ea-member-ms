package com.easyapper.member.exception;

public enum ErrorCode {
    NOT_FOUND(404), ALREADY_EXIST(409), BAD_REQUEST(400), SERVER_ERROR(500);
   
	private int val;
	
    ErrorCode(int val){
        this.val = val;
    }

    public int val(){
        return this.val;
    }

}
