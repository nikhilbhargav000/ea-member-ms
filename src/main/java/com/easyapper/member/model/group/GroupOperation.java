package com.easyapper.member.model.group;

public enum GroupOperation {
	JOIN_GROUP("joinGroup"), 
	LEAVE_GROUP("leaveGroup"), 
	INVITE_TO_GROUP("inviteToGroup"), 
	REVOKE_ADMIN_ROLE("revokeAdminRole"), 
	GRANT_ADMIN_ROLE("grantAdminRole"), 
	REMOVE_FROM_GROUP("removeFromGroup"), 
	DECLINE_INVITATION("declineInvitation");
	
	private final String value;
	
	private GroupOperation(final String value) {
		this.value = value;
	}

//	public String getValue() {
//		return this.value;
//	}
	
	public static GroupOperation getGroupOperation(final String value) {

		if (value.equals(GroupOperation.JOIN_GROUP.value)) {
			return GroupOperation.JOIN_GROUP;		
		} else if (value.equals(GroupOperation.LEAVE_GROUP.value)) {
			return GroupOperation.LEAVE_GROUP;
		} else if (value.equals(GroupOperation.INVITE_TO_GROUP.value)) {
			return GroupOperation.INVITE_TO_GROUP;
		} else if (value.equals(GroupOperation.REVOKE_ADMIN_ROLE.value)) {
			return GroupOperation.REVOKE_ADMIN_ROLE;
		} else if (value.equals(GroupOperation.GRANT_ADMIN_ROLE.value)) {
			return GroupOperation.GRANT_ADMIN_ROLE;
		} else if (value.equals(GroupOperation.REMOVE_FROM_GROUP.value)) {
			return GroupOperation.REMOVE_FROM_GROUP;
		}

		return null;
	}
	
	@Override
	public String toString() {
		return this.value.toLowerCase();
	}
	
}
