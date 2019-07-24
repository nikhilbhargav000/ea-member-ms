package com.easyapper.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.easyapper.member.model.ResponseMessage;
import com.easyapper.member.service.InvitationService;


@Controller
@RequestMapping(value="{appId}/users/{invitedUserId}/invitations/{invitationId}/")
public class InvitationController {

	private final InvitationService invitationService;

	@Autowired
	public InvitationController(InvitationService invitationService) {
		super();
		this.invitationService = invitationService;
	}
	
	@RequestMapping(value="accept", method=RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> acceptInvitation(@PathVariable("appId") String appId,
			@PathVariable("invitedUserId") String invitedUserId, @PathVariable("invitationId") String invitationId) throws Exception {
		
		ResponseMessage response = invitationService.acceptInvitation(appId, invitedUserId,
				invitationId);
		return new ResponseEntity<ResponseMessage>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="decline", method=RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> declineInvitation(@PathVariable("appId") String appId,
			@PathVariable("invitedUserId") String invitedUserId, @PathVariable("invitationId") String invitationId) throws Exception {
		
		ResponseMessage response = invitationService.declineInvitation(appId, invitedUserId,
				invitationId);
		return new ResponseEntity<ResponseMessage>(response, HttpStatus.OK);
	}
	
}
