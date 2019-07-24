package com.easyapper.member.service.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
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

/**
 * Check if there user have the admin access for the provided groupId
 * @author nikhil
 */
@Component
public class CheckAdminAccessCommand implements Command {

	private final GroupRepository groupRepo;
	
	@Autowired
	public CheckAdminAccessCommand(GroupRepository groupRepo) {
		this.groupRepo = groupRepo;
	}

	@Override
	public boolean execute(Context context) throws Exception {

		CommandOperationRequest ctxtRequest = (CommandOperationRequest) context.get(CommandContext.CONTEXT_REQUEST);

		if (StringUtils.isNotBlank(ctxtRequest.getUserId()) || StringUtils.isNotBlank(ctxtRequest.getGroupId())
				|| StringUtils.isNotBlank(ctxtRequest.getAppId())) {
			Group group = groupRepo.findGroup(ctxtRequest.getAppId(), ctxtRequest.getGroupId());
			if (group.getMembers() != null) {
				boolean validUser = false;
				for (GroupMember groupMember : group.getMembers()) {
					if (groupMember.getUserId().equals(ctxtRequest.getUserId())
							&& groupMember.getRole().toLowerCase().equals(GroupMemberRole.ADMIN.toString())) {
						validUser = true;
						break;
					}
				}
				if (validUser == false) {
					throw new EAMemRuntimeException(ErrorCode.FORBIDDEN,
							"User is should have admin access");
				}
			} else {
				throw new EAMemRuntimeException(ErrorCode.FORBIDDEN,
						"User is not a member for the group : " + ctxtRequest.getGroupId());
			}
		} else {
			return true;
		}

		return false;
	}

}
