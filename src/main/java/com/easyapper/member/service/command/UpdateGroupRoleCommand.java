package com.easyapper.member.service.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.model.group.GroupMember;
import com.easyapper.member.model.group.GroupMemberRole;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;

@Component
public class UpdateGroupRoleCommand implements Command {

	
	private final GroupRepository groupRepo;
	
	@Autowired
	public UpdateGroupRoleCommand(GroupRepository groupRepo) {
		this.groupRepo = groupRepo;
	}

	@Override
	public boolean execute(Context context) throws Exception {

		CommandOperationRequest ctxtRequest = (CommandOperationRequest) context.get(CommandContext.CONTEXT_REQUEST);

		if (CollectionUtils.isNotEmpty(ctxtRequest.getUpdateGroupRoleUserIds())) {
			if (StringUtils.isNotBlank(ctxtRequest.getAppId()) && StringUtils.isNotBlank(ctxtRequest.getGroupId())
					&& StringUtils.isNotEmpty(ctxtRequest.getUserId())) {
				ctxtRequest.getUpdateGroupRoleUserIds().stream().forEach((userId) -> {

					updateGroupMemberRole(ctxtRequest.getAppId(), userId, ctxtRequest.getGroupId(),
							ctxtRequest.getUpdateGroupMemberRole());

				});
			} else {
				throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
			}
		} else {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "userIds should not be empty");
		}

		return false;
	}
	
	private void updateGroupMemberRole(String appId, String userId, String groupId, GroupMemberRole updateMemberRole) {

		Group group = groupRepo.findGroup(appId, groupId);

		if (group != null) {
			boolean memberFound = false;
			for (GroupMember groupMember : group.getMembers()) {
				if (groupMember.getUserId().equals(userId)) {
					memberFound = true;
					groupMember.setRole(updateMemberRole.toString());
					break;
				}
			}
			if (memberFound) {
				groupRepo.updateGroupMembers(appId, group);
			}
		} else {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
	}
	
}
