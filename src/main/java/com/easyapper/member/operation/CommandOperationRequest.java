package com.easyapper.member.operation;

import java.util.List;
import java.util.Set;

import com.easyapper.member.model.InvitationStatus;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.model.group.GroupMember;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class CommandOperationRequest {

	private String appId;
	private String userId;
	private String groupId;

	private List<GroupMember> sendInviteGroupMembers;
	private List<String> setupInviteUserIds;
	
	private Set<GroupMember> addGroupForMembers;
	private Set<String> removeGroupForMembers;
	
	private Group group;
	
	private InvitationStatus updateInviteStatus;
	
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public List<GroupMember> getSendInviteGroupMembers() {
		return sendInviteGroupMembers;
	}
	public void setSendInviteGroupMembers(List<GroupMember> sendInviteGroupMembers) {
		this.sendInviteGroupMembers = sendInviteGroupMembers;
	}
	public Set<GroupMember> getAddGroupForMembers() {
		return addGroupForMembers;
	}
	public void setAddGroupForMembers(Set<GroupMember> addGroupForMembers) {
		this.addGroupForMembers = addGroupForMembers;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public InvitationStatus getUpdateInviteStatus() {
		return updateInviteStatus;
	}
	public void setUpdateInviteStatus(InvitationStatus updateInviteStatus) {
		this.updateInviteStatus = updateInviteStatus;
	}
	public Set<String> getRemoveGroupForMembers() {
		return removeGroupForMembers;
	}
	public void setRemoveGroupForMembers(Set<String> removeGroupForMembers) {
		this.removeGroupForMembers = removeGroupForMembers;
	}
	public List<String> getSetupInviteUserIds() {
		return setupInviteUserIds;
	}
	public void setSetupInviteUserIds(List<String> setupInviteUserIds) {
		this.setupInviteUserIds = setupInviteUserIds;
	}
	
}
