package com.easyapper.member.service.command;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.InvitationRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Invitation;
import com.easyapper.member.model.InvitationStatus;
import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.model.group.GroupValidator;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

/**
 *	Check if invitations exists in [sent] state
 *	And GroupMember for next command 
 * @author nikhil
 */
@Component
public class CheckInvitationExistCommand implements Command{

	private final GroupValidator groupValidator;
	private final InvitationRepository invitationRepo; 
	
	@Autowired
	public CheckInvitationExistCommand(GroupValidator groupValidator, InvitationRepository invitationRepo) {
		super();
		this.groupValidator = groupValidator;
		this.invitationRepo = invitationRepo;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) context.get(CommandContext.CONTEXT_REQUEST);

		String receiverUserId = ctxtRequest.getUserId();
		if (StringUtils.isEmpty(ctxtRequest.getAppId()) || StringUtils.isEmpty(ctxtRequest.getGroupId())
				|| StringUtils.isEmpty(receiverUserId)) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "AppId, GroupId, UserId cannot be empty");
		}

		if (!groupValidator.sentInvitationExists(ctxtRequest.getAppId(), receiverUserId,
				ctxtRequest.getGroupId())) {
			throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "Invitation not found");
		}
		Invitation sentInvitation = invitationRepo.findInvitationByReceiverIdGroupIdStatus(ctxtRequest.getAppId(), receiverUserId,
				ctxtRequest.getGroupId(), InvitationStatus.SENT.toString());
		this.updateRequestForNextCommand(ctxtRequest, sentInvitation);
		
		return false;
	}

	private void updateRequestForNextCommand(CommandOperationRequest ctxtRequest, Invitation invitation) {
		Set<GroupMember> groupMembers = new HashSet<>();
		groupMembers.add(new GroupMember(invitation.getReceiverUserId(), invitation.getMemberRole()));
		ctxtRequest.setAddGroupForMembers(groupMembers);
	}
	

}
