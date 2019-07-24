package com.easyapper.member.service.command;

import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.dao.MemberRepository;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

/**
 * Add the members for the specific group
 * @author nikhil
 */
@Component
public class RemoveGroupForMemberCommand implements Command{

	private final MemberRepository memberRepo;
	private final GroupRepository groupRepo;
	
	private final Logger logger = LoggerFactory.getLogger(RemoveGroupForMemberCommand.class);
	
	@Autowired
	public RemoveGroupForMemberCommand(MemberRepository memberRepo, GroupRepository groupRepo) {
		super();
		this.memberRepo = memberRepo;
		this.groupRepo = groupRepo;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		if (CollectionUtils.isEmpty(ctxtRequest.getRemoveGroupForMembers())) {
			logger.info("No UserIds for removing the groups");
			return true;
		}
		
		ctxtRequest.getRemoveGroupForMembers().stream().forEach((userId) -> {
			
			this.removeGroupInMember(ctxtRequest.getAppId(), userId, ctxtRequest.getGroupId());
			this.removeMemberInGroup(ctxtRequest.getAppId(), userId, ctxtRequest.getGroupId());
		});
		
		return false;
	}

	private void removeMemberInGroup(String appId, String userId, String groupId) {
		Group group = groupRepo.findGroup(appId, groupId);
		if (group != null) {
			Set<GroupMember> groupMembers = group.getMembers();
			GroupMember groupMember = new GroupMember(userId);
			if (groupMembers != null && groupMembers.contains(groupMember)) {
				groupMembers.remove(groupMember);
				groupRepo.updateGroupMembers(appId, group);
			} else {
				logger.info("UserId not found");
			}
		} else {
			logger.info("Group not found");
		}
	}
	
	private void removeGroupInMember(String appId, String userId, String groupId) {
		Member member = memberRepo.findMemberByUserId(appId, userId);
		if (member != null) {
			Set<String> groupIds = member.getGroupIds();
			if (groupIds != null && groupId != null && groupIds.contains(groupId)) {
				groupIds.remove(groupId);
				memberRepo.updateMember(appId, member);
			} else {
				logger.info("Unable to find GroupId in MemberGroups");
			}
		} else {
			logger.info("Member not found");
		}
	}

}
