package com.easyapper.member.model;

public enum InvitationStatus {
	SENT("sent"), ACCEPTED("accepted"), DECLINED("declined");
	
	private String value;
	
	private InvitationStatus(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value.toLowerCase();
	}
	
}
