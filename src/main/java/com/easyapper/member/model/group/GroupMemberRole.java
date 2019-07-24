package com.easyapper.member.model.group;

public enum GroupMemberRole {
	
	ADMIN("admin"), USER("user");

	
	private String value;
	
	private GroupMemberRole(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value.toLowerCase();
	}
}
