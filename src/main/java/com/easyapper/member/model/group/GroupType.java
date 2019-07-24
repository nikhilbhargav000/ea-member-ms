package com.easyapper.member.model.group;

public enum GroupType {
	
	PUBLIC("public"), PRIVATE("private");
	
	private String value;
	private GroupType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
