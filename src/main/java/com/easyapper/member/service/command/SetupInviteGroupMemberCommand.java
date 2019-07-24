package com.easyapper.member.service.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.model.group.GroupMemberRole;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

/**
 * Setup the SendInviteGroupMembers Set in the ContextRequest 
 * for next SendInvitationCommand
 * 
 * @author nikhil
 */
@Component
public class SetupInviteGroupMemberCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		
		if (CollectionUtils.isNotEmpty(ctxtRequest.getSetupInviteUserIds())) {
			
			List<GroupMember> sendInviteGroupMembers = new ArrayList<GroupMember>();
			ctxtRequest.getSetupInviteUserIds().stream().forEach((invitedUserId)-> {
				sendInviteGroupMembers.add(new GroupMember(invitedUserId, GroupMemberRole.USER.toString()));
			});
			
			ctxtRequest.setSendInviteGroupMembers(sendInviteGroupMembers);
			
		} else {
			return true;
		}
		
		return false;
	}
	
}
