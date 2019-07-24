package com.easyapper.member.service.command;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.DBSeqFinder;
import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.ResponseMessage;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.model.group.GroupMemberRole;
import com.easyapper.member.model.group.GroupType;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

@Component
public class CreateGroupCommand implements Command{

	
	private final GroupRepository groupRepo;	
	private final DBSeqFinder dbSeqFinder;
	
	@Autowired
	public CreateGroupCommand(GroupRepository groupRepo, DBSeqFinder dbSeqFinder) {
		this.groupRepo = groupRepo;
		this.dbSeqFinder = dbSeqFinder;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		
		Group group = ctxtRequest.getGroup();
		this.validateGroup(group, ctxtRequest);
		
		// Set GroupId
		group.setGroupId(dbSeqFinder.getNextValForGroup(ctxtRequest.getAppId()));
		
		// Set List for Group Members 
		group.setMembers(new HashSet<GroupMember>());
		
		//Other Fields
		group.setCreatedBy(ctxtRequest.getUserId());
		group.setCreatedAt(Instant.now().getEpochSecond());
		group.setActive(true);
		
		//Insert
		if(StringUtils.isNotBlank(ctxtRequest.getAppId())) {
			groupRepo.createGroup(ctxtRequest.getAppId(), group);
		} else {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		
		this.updateRequestForNextCommand(ctxtRequest, group);
		this.updateResponse(context, group);
		
		return false;
	}
	
	private void updateRequestForNextCommand(CommandOperationRequest ctxtRequest, Group group) {

		Set<GroupMember> groupMembers = new HashSet<>();
		groupMembers.add(new GroupMember(ctxtRequest.getUserId(), GroupMemberRole.ADMIN.toString()));

		//set Other member in Request for AddMemberForGroups Command
		ctxtRequest.setGroupId(group.getGroupId());
		ctxtRequest.setAddGroupForMembers(groupMembers);
	}
	
	private void updateResponse(Context context, Group group) {
		//Set response 
		if(context.get(CommandContext.CONTEXT_RESPONSE) != null) {
			ResponseMessage response = (ResponseMessage) context.get(CommandContext.CONTEXT_RESPONSE);
			response.setId(group.getGroupId());
		}
	}
	
	private void validateGroup(Group group, CommandOperationRequest ctxtRequest) {
		
		if(StringUtils.isBlank(ctxtRequest.getAppId())) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "AppId cannot be blank");
		}
		
		if (StringUtils.isBlank(group.getName())) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid Group Name");
		}
		if (group.getGroupId() != null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid GroupId should be null");
		}
		if (!group.getType().toLowerCase().equals(GroupType.PUBLIC.getValue())
				&& !group.getType().toLowerCase().equals(GroupType.PRIVATE.getValue())) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid Group Type should be public or private");
		}
		if (group.getCreatedBy() != null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid Group CreatedBy should be null");
		}
	}

}
