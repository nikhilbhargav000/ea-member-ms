package com.easyapper.member.service.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.group.GroupValidator;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

/**
 * Check if member for group
 * @author nikhil
 */
@Component
public class IsMemberOfGroupCommand implements Command{

	private final GroupValidator groupValidator;
	
	@Autowired
	public IsMemberOfGroupCommand(GroupValidator groupValidator) {
		super();
		this.groupValidator = groupValidator;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		
		if (StringUtils.isEmpty(ctxtRequest.getAppId()) 
				|| StringUtils.isEmpty(ctxtRequest.getGroupId()) 
				|| StringUtils.isEmpty(ctxtRequest.getUserId())) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "AppId , userId, GroupId "
					+ "cannot be empty");
		}
		
		if (!groupValidator.isUserMemberOfGroup(ctxtRequest.getAppId(), ctxtRequest.getUserId(), 
				ctxtRequest.getGroupId())) {
			throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "User group not found for given details");
		}
		
		return false;
	}

	
}
