package com.easyapper.member.exception;

import com.easyapper.member.model.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MemberAppException {
    @ExceptionHandler(MemberRuntimeException.class)
    public ResponseEntity<ResponseMessage> handle(MemberRuntimeException e)
    {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus("Failed");
        responseMessage.setMessage("Error !!!");
        return ResponseEntity.status(e.code).body(responseMessage);
    }
}
