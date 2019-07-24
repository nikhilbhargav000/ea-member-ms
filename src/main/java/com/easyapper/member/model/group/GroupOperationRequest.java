package com.easyapper.member.model.group;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter @Getter
@ToString
public class GroupOperationRequest {

	private String operation;
	private String targetUserId;
	private List<String> targetUserIds;
}
