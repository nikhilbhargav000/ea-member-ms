package com.easyapper.member.service.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.model.group.GroupMemberRole;
import com.easyapper.member.model.group.GroupValidator;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

/**
 * Check any group admin exists specific group
 * if not then set one person as admin
 * @author nikhil
 */

@Component
public class GroupAdminExistCheckCommand implements Command {

	private final GroupValidator groupValidator;
	private final GroupRepository groupRepo;
	
	@Autowired
	public GroupAdminExistCheckCommand(GroupValidator groupValidator, GroupRepository groupRepo) {
		this.groupValidator = groupValidator;
		this.groupRepo = groupRepo;
	}


	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		
		if (StringUtils.isBlank(ctxtRequest.getGroupId()) &&
				StringUtils.isBlank(ctxtRequest.getAppId())) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		
		if (!groupValidator.adminExistInGroup(ctxtRequest.getAppId(), ctxtRequest.getGroupId())) {
			Group group = groupRepo.findGroup(ctxtRequest.getAppId(), ctxtRequest.getGroupId());
			// If No admin exists,then set one person as admin
			if (CollectionUtils.isNotEmpty(group.getMembers())) {
				group.getMembers().iterator().next().setRole(GroupMemberRole.ADMIN.toString());
				groupRepo.updateGroupMembers(ctxtRequest.getAppId(), group);
			}
			
		}
		
		return false;
	}

}
