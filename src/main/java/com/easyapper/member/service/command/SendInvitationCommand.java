package com.easyapper.member.service.command;

import java.time.Instant;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.DBSeqFinder;
import com.easyapper.member.dao.InvitationRepository;
import com.easyapper.member.model.Invitation;
import com.easyapper.member.model.InvitationStatus;
import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.model.group.GroupValidator;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;


/*
 * Send invitations to SendInviteGroupMembers (Of CommandRequest)
 * (Considers the userId(s) and role(s) are valid)
 */
@Component
public class SendInvitationCommand implements Command{

	private final InvitationRepository invitationRepo;
	private final GroupValidator groupValidator;
	private final DBSeqFinder dbSeqFinder;
	
	private Logger logger = LoggerFactory.getLogger(SendInvitationCommand.class);
	
	@Autowired
	public SendInvitationCommand(InvitationRepository invitationRepo, GroupValidator groupValidator,
			DBSeqFinder dbSeqFinder) {
		super();
		this.invitationRepo = invitationRepo;
		this.groupValidator = groupValidator;
		this.dbSeqFinder = dbSeqFinder;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		CommandOperationRequest ctxtRequest = (CommandOperationRequest) 
				context.get(CommandContext.CONTEXT_REQUEST);
		
		List<GroupMember> targetGroupMembers = ctxtRequest.getSendInviteGroupMembers();
		if (CollectionUtils.isNotEmpty(targetGroupMembers) && StringUtils.isNotBlank(ctxtRequest.getAppId())
				&& StringUtils.isNotBlank(ctxtRequest.getGroupId())
				&& StringUtils.isNotBlank(ctxtRequest.getUserId())) {
			
			targetGroupMembers.stream().forEach((groupMember) -> {
				if (groupValidator.isValidGroupMemberRole(groupMember.getRole())) {
					this.sendInvitation(groupMember, ctxtRequest.getAppId(), ctxtRequest.getGroupId(),
							ctxtRequest.getUserId());
				}
			});
		}
		
		return false;
	}

	private void sendInvitation(GroupMember groupMember, String appId, String groupId, 
			String senderUserId) {
		String receiverUserId = groupMember.getUserId();
		Invitation invitation = Invitation.builder()
				.invitationId(dbSeqFinder.getNextValForInvitation(appId))
				.groupId(groupId)
				.senderUserId(senderUserId)
				.receiverUserId(receiverUserId)
				.status(InvitationStatus.SENT.toString())
				.sentAt(Instant.now().getEpochSecond())
				.memberRole(groupMember.getRole().toLowerCase())
				.build();
		
		if (!groupValidator.sentInvitationAlreadyExist(appId, senderUserId, receiverUserId, groupId) 
				&& !groupValidator.isUserMemberOfGroup(appId, receiverUserId, groupId)) {
			invitationRepo.insert(appId, invitation);
		} else {
			logger.info("Invitation already exist : " + invitation);
		}
	}
	
	

}
