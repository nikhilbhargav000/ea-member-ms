package com.easyapper.member.operation;

import org.apache.commons.chain.impl.ContextBase;

import com.easyapper.member.model.ResponseMessage;

public class CommandContext extends ContextBase{

	public static final String CONTEXT_REQUEST = "ctxtRequest";
	public static final String CONTEXT_RESPONSE = "ctxtResponse";

	public CommandOperationRequest getContextRequest() {
		return (CommandOperationRequest) this.get(CONTEXT_REQUEST);
	}

	public void setContextRequest(CommandOperationRequest contextRequest) {
		this.put(CONTEXT_REQUEST, contextRequest);
	}
	

	public ResponseMessage getContextResponse() {
		return (ResponseMessage) this.get(CONTEXT_RESPONSE);
	}
	
	public void setContextResponse(ResponseMessage contextResponse) {
		this.put(CONTEXT_RESPONSE, contextResponse);
	}
	
}
