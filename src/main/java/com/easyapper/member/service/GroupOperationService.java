package com.easyapper.member.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.InvitationStatus;
import com.easyapper.member.model.ResponseMessage;
import com.easyapper.member.model.group.GroupMemberRole;
import com.easyapper.member.model.group.GroupOperation;
import com.easyapper.member.model.group.GroupOperationRequest;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;
import com.easyapper.member.service.command.AddGroupForMemberCommand;
import com.easyapper.member.service.command.CheckAdminAccessCommand;
import com.easyapper.member.service.command.CheckInvitationExistCommand;
import com.easyapper.member.service.command.GroupAdminExistCheckCommand;
import com.easyapper.member.service.command.IsMemberOfGroupCommand;
import com.easyapper.member.service.command.RemoveGroupForMemberCommand;
import com.easyapper.member.service.command.SendInvitationCommand;
import com.easyapper.member.service.command.SetupInviteGroupMemberCommand;
import com.easyapper.member.service.command.UpdateGroupRoleCommand;
import com.easyapper.member.service.command.UpdateInvitationCommand;
import com.easyapper.member.service.command.ValidateGroupOperationRequestCommand;

@Service
public class GroupOperationService {

	private final ApplicationContext appContext;

	@Autowired
	public GroupOperationService(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public ResponseMessage performOperation(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest) throws Exception {

		GroupOperation operation = this.getOperation(operationRequest);
		if (operation == null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid GroupOperation");
		}

		ResponseMessage response = performOperation(appId, userId, groupId, operationRequest, operation);
		response.setStatus("Success");
		response.setMessage("Operation Successful");
		return response;
	}

	public ResponseMessage performOperation(final String appId, final String userId, final String groupId,
			GroupOperation operation) throws Exception {
		return performOperation(appId, userId, groupId, null, operation);
	}

	/**
	 * 
	 * Perform Group Operation (for opertion inputed)
	 * 
	 * Response code 200 (If successful)
	 * 
	 * @param appId
	 * @param userId
	 * @param groupId
	 * @param operationRequest
	 * @param operation
	 * @return
	 * @throws Exception
	 */

	public ResponseMessage performOperation(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest, GroupOperation operation) throws Exception {
		ResponseMessage response = new ResponseMessage();

		switch (operation) {
		case JOIN_GROUP:
			response = joinGroup(appId, userId, groupId, operationRequest);
			break;
		case DECLINE_INVITATION:
			response = declineInvitation(appId, userId, groupId);
			break;
		case LEAVE_GROUP:
			response = leaveGroup(appId, userId, groupId, operationRequest);
			break;
		case INVITE_TO_GROUP:
			response = inviteToGroup(appId, userId, groupId, operationRequest);
			break;
		case REMOVE_FROM_GROUP:
			response = removeFromGroup(appId, userId, groupId, operationRequest);
			break;
		case REVOKE_ADMIN_ROLE:
			response = revokeAdminRole(appId, userId, groupId, operationRequest);
			break;
		case GRANT_ADMIN_ROLE:
			response = grantAdminRole(appId, userId, groupId, operationRequest);
			break;
		}

		return response;
	}

	private GroupOperation getOperation(GroupOperationRequest operationRequest) {
		GroupOperation operation = null;
		if (StringUtils.isNotBlank(operationRequest.getOperation())) {
			operation = GroupOperation.getGroupOperation(operationRequest.getOperation());
		}
		return operation;
	}


	private ResponseMessage revokeAdminRole(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest) throws Exception {
		
		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroupId(groupId);
		
		contextRequest.setUpdateGroupRoleUserIds(operationRequest.getTargetUserIds());
		contextRequest.setUpdateGroupMemberRole(GroupMemberRole.USER);

		// Execute Chain
		ChainBase chainBase = new ChainBase();
		
		chainBase.addCommand(appContext.getBean(ValidateGroupOperationRequestCommand.class));
		chainBase.addCommand(appContext.getBean(CheckAdminAccessCommand.class));
		chainBase.addCommand(appContext.getBean(UpdateGroupRoleCommand.class));

		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);

		return context.getContextResponse();
	}
	private ResponseMessage grantAdminRole(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest) throws Exception {
		
		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroupId(groupId);
		
		contextRequest.setUpdateGroupRoleUserIds(operationRequest.getTargetUserIds());
		contextRequest.setUpdateGroupMemberRole(GroupMemberRole.ADMIN);

		// Execute Chain
		ChainBase chainBase = new ChainBase();
		
		chainBase.addCommand(appContext.getBean(ValidateGroupOperationRequestCommand.class));
		chainBase.addCommand(appContext.getBean(CheckAdminAccessCommand.class));
		chainBase.addCommand(appContext.getBean(UpdateGroupRoleCommand.class));

		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);

		return context.getContextResponse();
	}
	
	private ResponseMessage inviteToGroup(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest) throws Exception {
		
		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroupId(groupId);
		
		contextRequest.setSetupInviteUserIds(operationRequest.getTargetUserIds());

		// Execute Chain
		ChainBase chainBase = new ChainBase();
		
		chainBase.addCommand(appContext.getBean(ValidateGroupOperationRequestCommand.class));
		chainBase.addCommand(appContext.getBean(CheckAdminAccessCommand.class));
		chainBase.addCommand(appContext.getBean(SetupInviteGroupMemberCommand.class));
		chainBase.addCommand(appContext.getBean(SendInvitationCommand.class));

		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);

		return context.getContextResponse();
	}
	
	private ResponseMessage removeFromGroup(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest) throws Exception {

		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroupId(groupId);
		 
		Set<String> removeMembers = null;
		if (CollectionUtils.isNotEmpty(operationRequest.getTargetUserIds())) {
			removeMembers = new HashSet<>(operationRequest.getTargetUserIds());
		}
		contextRequest.setRemoveGroupForMembers(removeMembers);

		// Execute Chain
		ChainBase chainBase = new ChainBase();
		
		chainBase.addCommand(appContext.getBean(ValidateGroupOperationRequestCommand.class));
		chainBase.addCommand(appContext.getBean(CheckAdminAccessCommand.class));
		chainBase.addCommand(appContext.getBean(RemoveGroupForMemberCommand.class));
		chainBase.addCommand(appContext.getBean(GroupAdminExistCheckCommand.class));

		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);

		return context.getContextResponse();
	}
	
	private ResponseMessage leaveGroup(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest) throws Exception {
		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroupId(groupId);
		 
		Set<String> removeMembers = new HashSet<>();
		removeMembers.add(userId);
		contextRequest.setRemoveGroupForMembers(removeMembers);

		// Execute Chain
		ChainBase chainBase = new ChainBase();
		
		chainBase.addCommand(appContext.getBean(ValidateGroupOperationRequestCommand.class));
		chainBase.addCommand(appContext.getBean(IsMemberOfGroupCommand.class));
		chainBase.addCommand(appContext.getBean(RemoveGroupForMemberCommand.class));
		chainBase.addCommand(appContext.getBean(GroupAdminExistCheckCommand.class));

		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);

		return context.getContextResponse();
	}

	private ResponseMessage declineInvitation(final String appId, final String userId, final String groupId)
			throws Exception {
		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroupId(groupId);
		contextRequest.setUpdateInviteStatus(InvitationStatus.DECLINED);

		// Execute Chain
		ChainBase chainBase = new ChainBase();
		chainBase.addCommand(appContext.getBean(ValidateGroupOperationRequestCommand.class));
		chainBase.addCommand(appContext.getBean(CheckInvitationExistCommand.class));
		chainBase.addCommand(appContext.getBean(UpdateInvitationCommand.class));

		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);

		return context.getContextResponse();
	}

	private ResponseMessage joinGroup(final String appId, final String userId, final String groupId,
			GroupOperationRequest operationRequest) throws Exception {

		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroupId(groupId);
		contextRequest.setUpdateInviteStatus(InvitationStatus.ACCEPTED);

		// Execute Chain
		ChainBase chainBase = new ChainBase();
		chainBase.addCommand(appContext.getBean(ValidateGroupOperationRequestCommand.class));
		chainBase.addCommand(appContext.getBean(CheckInvitationExistCommand.class));
		chainBase.addCommand(appContext.getBean(AddGroupForMemberCommand.class));
		chainBase.addCommand(appContext.getBean(UpdateInvitationCommand.class));

		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);

		return context.getContextResponse();
	}

}
