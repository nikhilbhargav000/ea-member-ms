package com.easyapper.member.model.group;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.InvitationRepository;
import com.easyapper.member.dao.MemberRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Invitation;
import com.easyapper.member.model.InvitationStatus;
import com.easyapper.member.model.Member;

@Component
public class GroupValidator {

	private final MemberRepository memberRepo;
	private final InvitationRepository invitationRepo;
	
	@Autowired
	public GroupValidator(MemberRepository memberRepo, InvitationRepository invitationRepo) {
		super();
		this.memberRepo = memberRepo;
		this.invitationRepo = invitationRepo;
	}

	public boolean isValidGroupMemberRole(String role) {
		if (StringUtils.isBlank(role)) {
			return false;
		}
		if (role.toLowerCase().equals(GroupMemberRole.ADMIN.toString().toLowerCase())
				|| role.toLowerCase().equals(GroupMemberRole.USER.toString().toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public boolean isValidGroupType(String groupType) {
		if (StringUtils.isBlank(groupType)) {
			return false;
		}
		if (groupType.toLowerCase().equals(GroupType.PUBLIC.getValue().toLowerCase())
				|| groupType.toLowerCase().equals(GroupType.PRIVATE.getValue().toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public boolean sentInvitationAlreadyExist(String appId, String senderUserId, String receiverUserId, String groupId) {
		
		Invitation sentInvitation = invitationRepo.findInvitation(appId, senderUserId, receiverUserId, groupId,
				InvitationStatus.SENT.toString());
		
		if (sentInvitation == null) {
			return false;
		}
		return true;
	}
	
	public boolean sentInvitationExists(String appId, String receiverUserId, String groupId) {
		Invitation sentInvitation = invitationRepo.findInvitationByReceiverIdGroupIdStatus(appId,
				receiverUserId, groupId, InvitationStatus.SENT.toString());
		if (sentInvitation == null) {
			return false;
		}
		return true;
	}
	
	public boolean isUserMemberOfGroup(String appId, String receiverUserId, String groupId) {

		Member member = memberRepo.findMemberByUserId(appId, receiverUserId);
		if (member == null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		
		if (member.getGroupIds() != null && member.getGroupIds().contains(groupId)) {
			return true;
		}
		return false;
	}
	
}
