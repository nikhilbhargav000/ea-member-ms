package com.easyapper.member.service.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.InvitationRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Invitation;
import com.easyapper.member.model.InvitationStatus;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

@Component
public class UpdateInvitationCommand implements Command{

	private final InvitationRepository invitationRepo;
	
	@Autowired
	public UpdateInvitationCommand(InvitationRepository invitationRepo) {
		this.invitationRepo = invitationRepo;
	}
	
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		
		String receiverUserId = ctxtRequest.getUserId();
		Invitation invitation = invitationRepo.findInvitationByReceiverIdGroupIdStatus(ctxtRequest.getAppId(),
				receiverUserId, ctxtRequest.getGroupId(), InvitationStatus.SENT.toString());
		
		if (invitation == null) {
			throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "Invitation not found");
		}
		
		if (ctxtRequest.getUpdateInviteStatus() != null) { 
			invitation.setStatus(ctxtRequest.getUpdateInviteStatus().toString());
			invitationRepo.updateInvitation(ctxtRequest.getAppId(), invitation);
		}
		return false;
	}

}
