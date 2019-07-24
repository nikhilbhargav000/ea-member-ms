package com.easyapper.member.model.group;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class GroupMember {
	
	private String userId;
	private String role;
	

	public GroupMember(String userId) {
		super();
		this.userId = userId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		GroupMember groupMember = (GroupMember) obj;
		return this.userId.equals(groupMember.getUserId());
	}

	
}
