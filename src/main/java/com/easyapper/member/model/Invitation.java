package com.easyapper.member.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Builder
@ToString
public class Invitation {
	@Id
	private ObjectId id;
	
	private String invitationId;
	private String senderUserId;
	private String receiverUserId;
	private String groupId;
    private String status;
    
    // Can be Admin or User
    private String memberRole;
    
    private long sentAt;
    private long receiverAt;
    
    
	public Invitation(String invitationId, String senderUserId, String receiverUserId, String groupId, String status,
			String memberRole, long sentAt, long receiverAt) {
		super();
		this.invitationId = invitationId;
		this.senderUserId = senderUserId;
		this.receiverUserId = receiverUserId;
		this.groupId = groupId;
		this.status = status;
		this.memberRole = memberRole;
		this.sentAt = sentAt;
		this.receiverAt = receiverAt;
	}
    
    
	
}
