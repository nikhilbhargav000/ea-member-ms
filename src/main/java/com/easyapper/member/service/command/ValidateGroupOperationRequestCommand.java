package com.easyapper.member.service.command;

import java.util.Collection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.dao.MemberRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.model.group.GroupValidator;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

@Component
public class ValidateGroupOperationRequestCommand implements Command  {

	private final MemberRepository memberRepo;
	private final GroupRepository groupRepo;
	private final GroupValidator groupValidator;

    
    @Autowired
    public ValidateGroupOperationRequestCommand(MemberRepository memberRepo, GroupRepository groupRepo, 
    		GroupValidator groupValidator){
        this.memberRepo = memberRepo;
        this.groupRepo = groupRepo;
        this.groupValidator = groupValidator;
    }

	
	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);

		this.isValidUserId(ctxtRequest.getAppId(), ctxtRequest.getUserId());
		this.isValidUserIds(ctxtRequest.getAppId(), ctxtRequest.getRemoveGroupForMembers());
		this.isValidUserIds(ctxtRequest.getAppId(), ctxtRequest.getSetupInviteUserIds());
		
		
		this.isValidGroupMembers(ctxtRequest.getAppId(), ctxtRequest.getAddGroupForMembers());
		this.isValidGroupMembers(ctxtRequest.getAppId(), ctxtRequest.getSendInviteGroupMembers());
		
		this.isValidGroupId(ctxtRequest.getAppId(), ctxtRequest.getGroupId());
		
		return false;
	}
	
	public void isValidGroupId(String appId, String groupId) {
		if(groupId == null) {
			return ;
		}
		if(StringUtils.isEmpty(appId) || StringUtils.isEmpty(groupId)) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		if (groupRepo.findGroup(appId, groupId) == null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid groupId or AppId");
		}
	}
	
	public void isValidGroupMembers(String appId, Collection<GroupMember> groupMembers) {
		if(StringUtils.isEmpty(appId)) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		
		if(CollectionUtils.isNotEmpty(groupMembers)) {
			groupMembers.stream().forEach((targetGroupMember)->{
				this.isValidGroupMember(appId, targetGroupMember);
			});
		}
	}
	
	private void isValidGroupMember(String appId, GroupMember groupMember) {
		if(groupMember == null) {
			return ;
		}
		if (groupMember.getUserId() == null || appId == null|| 
				memberRepo.findMemberByUserId(appId, groupMember.getUserId()) == null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid GroupMember UserId : "
					+ groupMember.getUserId());
		}
		if (!groupValidator.isValidGroupMemberRole(groupMember.getRole())) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid GroupMember Role : "
					+ "" + groupMember.getRole());
		}
	}
	
	public void isValidUserIds(String appId, Collection<String> userIds) {
		if (userIds == null) {
			return;
		}
		
		userIds.stream().forEach((userId) -> {
			isValidUserId(appId, userId);
		});
		return;
	}
	
	public boolean isValidUserId(String appId, String userId) {
		if (userId == null || appId == null) {	// If value is not there then we can go ahead
			return true;
		}
		if (memberRepo.findMemberByUserId(appId, userId) != null) {
			return true;
		}
		throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid appId :"
				+ appId + " or userId : " + userId);
	}

}
