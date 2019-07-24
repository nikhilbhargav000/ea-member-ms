package com.easyapper.member.service.command;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.dao.MemberRepository;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.model.group.GroupValidator;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

/**
 * Add the members for the specific group
 * @author nikhil
 */
@Component
public class AddGroupForMemberCommand implements Command{

	private final MemberRepository memberRepo;
	private final GroupRepository groupRepo;
	private final GroupValidator groupValidator;
	
	private final Logger logger = LoggerFactory.getLogger(AddGroupForMemberCommand.class);
	
	@Autowired
	public AddGroupForMemberCommand(MemberRepository memberRepo, GroupRepository groupRepo, 
			GroupValidator groupValidator) {
		this.memberRepo = memberRepo;
		this.groupRepo = groupRepo;
		this.groupValidator = groupValidator;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		
		if (CollectionUtils.isNotEmpty(ctxtRequest.getAddGroupForMembers())) {
			ctxtRequest.getAddGroupForMembers().stream().forEach((groupMember) -> {
				
				this.addMemberInGroup(ctxtRequest, groupMember);
				this.addGroupInMember(ctxtRequest, groupMember);
				
			});
		} else {
			logger.info("AddGroupForMemberUserIds lst found to be empty {}", ctxtRequest.getAddGroupForMembers());
			return true;
		}
		
		
		return false;
	}

	private void addMemberInGroup(CommandOperationRequest ctxtRequest, GroupMember targetGroupMember) {
	
		Group group = groupRepo.findGroup(ctxtRequest.getAppId(), ctxtRequest.getGroupId());
		if (group != null) {
			Set<GroupMember> groupMembers = group.getMembers();
			
			if (groupMembers != null) {
				if (groupMembers.contains(targetGroupMember)) {
					logger.info("User already added in group userId : {}", targetGroupMember.getUserId());
					return;
				}
				if (groupValidator.isValidGroupMemberRole(targetGroupMember.getRole())){
					// Add Group Member
					groupMembers.add(targetGroupMember);
					groupRepo.updateGroupMembers(ctxtRequest.getAppId(), group);
				}
			} else {
				logger.info("GroupMember list is null for {}", group);
			}
		} else {
			logger.info("Unable to find group CommandRequest : {}", ctxtRequest);
		}
	}

	private void addGroupInMember(CommandOperationRequest ctxtRequest, GroupMember targetGroupMember) {
		
		Member member = memberRepo.findMemberByUserId(ctxtRequest.getAppId(), targetGroupMember.getUserId());
		if (member != null || StringUtils.isNotBlank(ctxtRequest.getGroupId()) 
				|| StringUtils.isNotBlank(ctxtRequest.getAppId())) {
			Set<String> groupIds = member.getGroupIds();
			if (groupIds != null && !groupIds.contains(ctxtRequest.getGroupId())) {
			
				groupIds.add(ctxtRequest.getGroupId());
				memberRepo.updateMember(ctxtRequest.getAppId(), member);
			} else if (groupIds == null) {
				groupIds = new HashSet<>();
				groupIds.add(ctxtRequest.getGroupId());
				member.setGroupIds(groupIds);;
				memberRepo.updateMember(ctxtRequest.getAppId(), member);
			}
		} else {
			logger.info("Unable to find member for GroupMember : {} or the groupId : {}", 
					targetGroupMember, ctxtRequest.getGroupId());
		}
	}
	
	
}
