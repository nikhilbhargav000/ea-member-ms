package com.easyapper.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easyapper.member.dao.InvitationRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Invitation;
import com.easyapper.member.model.ResponseMessage;
import com.easyapper.member.model.group.GroupOperation;

@Service
public class InvitationService {

	private final GroupOperationService groupOperationService;
	private final InvitationRepository invitationRepo;

	@Autowired
	public InvitationService(GroupOperationService groupOperationService, InvitationRepository invitationRepo) {
		super();
		this.groupOperationService = groupOperationService;
		this.invitationRepo = invitationRepo;
	}

	public ResponseMessage acceptInvitation(String appId, String invitedUserId, String invitationId) throws Exception {
		Invitation invitation = invitationRepo.findInvitationByInvitationIdReceiverId(appId, invitationId,
				invitedUserId);
		if (invitation == null) {
			throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "Invalid invitationId or AppId");
		}
		ResponseMessage response = groupOperationService.performOperation(appId, invitedUserId, invitation.getGroupId(),
				GroupOperation.JOIN_GROUP);
		response.setStatus("Success");
		response.setMessage("Operation Successful");
		return response;
	}

	public ResponseMessage declineInvitation(String appId, String invitedUserId, String invitationId) throws Exception {
		Invitation invitation = invitationRepo.findInvitationByInvitationIdReceiverId(appId, invitationId,
				invitedUserId);
		if (invitation == null) {
			throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "Invalid invitationId or AppId");
		}
		ResponseMessage response = groupOperationService.performOperation(appId, invitedUserId, invitation.getGroupId(),
				GroupOperation.DECLINE_INVITATION);
		response.setStatus("Success");
		response.setMessage("Operation Successful");
		return response;
	}
	
}
