package com.easyapper.member.controller;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.ResponseMessage;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.model.group.GroupOperationRequest;
import com.easyapper.member.service.GroupOperationService;
import com.easyapper.member.service.GroupService;
import com.easyapper.member.service.UserService;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class UserController {
	private final UserService userSrv;
	private final GroupService groupService;
	private final GroupOperationService groupOperationService;

	@Autowired
	public UserController(UserService userSrv, GroupService groupService, GroupOperationService groupOperationService) {
		this.userSrv = userSrv;
		this.groupService = groupService;
		this.groupOperationService = groupOperationService;
	}

	@RequestMapping(value = "/{appId}/users", method = RequestMethod.POST)
	public ResponseEntity<ResponseMessage> registerUser(@PathVariable("appId") String appId, @RequestBody Member user) {
		ResponseMessage resMsg = new ResponseMessage();
		HttpStatus status = HttpStatus.CREATED;
		if (user.getUserId() == null || user.getUserId().isEmpty() || // For Validation
				user.getName() == null || user.getName().isEmpty()) {
			resMsg.setStatus("Failed");
			resMsg.setMessage("Mandatory field missing");
			status = HttpStatus.BAD_REQUEST;
		} else if (userSrv.findMemberByUserId(appId, user.getUserId()) == null) {
			ObjectId id = userSrv.createUser(appId, user);

			resMsg.setId(id.toHexString());
			resMsg.setStatus("Success");
			resMsg.setMessage("User created successfully");
		} else {
			resMsg.setStatus("Failed");
			resMsg.setMessage("userId already taken");
			status = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<>(resMsg, status);
	}

	@RequestMapping(value = "/{appId}/users/{userId}", method = RequestMethod.GET)
	public Member findMember(@PathVariable("appId") String appId, @PathVariable("userId") String userId) {
		Member member = userSrv.findMemberByUserId(appId, userId);
		if (member == null) {
			throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "user does not exist");
		}
		// member.setGroups(null);
		return member;
	}

	@RequestMapping(value = "/{appId}/users", method = RequestMethod.GET)
	public List<Member> findMembers(@PathVariable("appId") String appId, 
			@RequestParam Map<String, String> paramMap) {
		return userSrv.findUsers(appId, paramMap);
	}
    
    
    
    
	@RequestMapping(value = "/{appId}/users/{userId}/groups", method = RequestMethod.POST)
	public ResponseEntity<ResponseMessage> registerGroup(@PathVariable("appId") String appId,
			@PathVariable("userId") String userId, @RequestBody Group group) throws Exception {
		ResponseMessage resMsg = new ResponseMessage();
		HttpStatus status = HttpStatus.CREATED;

		resMsg = groupService.createGroup(appId, userId, group);
		resMsg.setStatus("Success");
		resMsg.setMessage("User Group created successfully");

		return new ResponseEntity<>(resMsg, status);
	}

	@RequestMapping(value = "/{appId}/users/{userId}/groups/{groupId}/operations", method = RequestMethod.POST
			, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseMessage> opertionOnUserGroup(@PathVariable("appId") String appId, @PathVariable("userId") String userId,
			@PathVariable("groupId") String groupId, @RequestBody GroupOperationRequest request) throws Exception {
		
		ResponseMessage resMsg = groupOperationService.performOperation(appId, userId, groupId, request);
		return new ResponseEntity<>(resMsg, HttpStatus.OK);
	}

	/**
	 * Method not to be supported yet
	 * 
	 * @param appId
	 * @param groupId
	 * @param members
	 * @return
	 */
	@RequestMapping(value = "/{appId}/users/{userId}/groups/{groupId}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> addGroupMembers(@PathVariable("appId") String appId,
			@PathVariable("groupId") String groupId, @RequestBody List<Member> members) {
		ResponseMessage resMsg = new ResponseMessage();
		HttpStatus status = HttpStatus.OK;

		Group grp = groupService.findGroup(appId, groupId);
		if (grp == null || !grp.getGroupId().equals(grp.getGroupId())) {
			resMsg.setStatus("Failed");
			resMsg.setMessage("Mandatory field missing or invalid");
			status = HttpStatus.BAD_REQUEST;
		} else {
			groupService.addGroupMembers(appId, grp, members);
			resMsg.setId(null);
			resMsg.setStatus("Success");
			resMsg.setMessage("Group members added successfully");
		}

		return new ResponseEntity<>(resMsg, status);
	}

	@RequestMapping(value = "/{appId}/users/{userId}/groups", method = RequestMethod.GET)
	public List<Group> findUserGroups(@PathVariable("appId") String appId, @PathVariable("userId") String userId) {
		List<Group> groups = userSrv.findUserGroups(appId, userId);
		return groups;
	}

	@RequestMapping(value = "/{appId}/users/{userId}/groups/{groupId}", method = RequestMethod.GET)
	public Group findUserGroup(@PathVariable("appId") String appId, @PathVariable("userId") String userId,
			@PathVariable("groupId") String groupId) {
		if (userSrv.findMemberByUserId(appId, userId) == null) {
			throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "user does not exist");
		} else {
			return groupService.findGroup(appId, groupId);
		}
	}
}
