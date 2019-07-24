package com.easyapper.member.exception;

import com.easyapper.member.model.ResponseMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EAMemExceptionHandler {
	
	Logger logger = LoggerFactory.getLogger(EAMemExceptionHandler.class);
	
    @ExceptionHandler(EAMemRuntimeException.class)
    public ResponseEntity<ResponseMessage> handle(EAMemRuntimeException e)
    {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus("Failed");
        if (StringUtils.isBlank(e.getMessage())) {
        	responseMessage.setMessage("Error !!!");
        } else {
        	responseMessage.setMessage(e.getMessage());
        }
        
        logger.debug("EAMemRuntimeException : ", e);
        
        return ResponseEntity.status(e.code).body(responseMessage);
    }
    
    @ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseMessage> handle(Exception e) {
    	  ResponseMessage responseMessage = new ResponseMessage();
          responseMessage.setStatus("Failed");
          
          logger.error("Exception occurred", e);
          
          return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(responseMessage);
	}
    
}
