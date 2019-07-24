package com.easyapper.member.dao;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Invitation;

@Repository
public class InvitationRepository {
	
	private final MongoTemplate mongoTemplate;
	
	private static class DBKeys{

		private static final String INVITATION_COLLECTION_POSTFIX = "_invitations";
		private static final String INVITATION_ID = "invitationId";
		private static final String SENDER_USER_ID = "senderUserId";
		private static final String RECEIVER_USER_ID = "receiverUserId";
		private static final String GROUP_ID = "groupId";
		private static final String STATUS = "status";
		private static final String SENT_AT = "sentAt";
	}
	
	public InvitationRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public static String getInvitationCollectionPostfix() {
		return DBKeys.INVITATION_COLLECTION_POSTFIX;
	}
	
	
	
	public Invitation insert(String appId, Invitation invitation) {
		if(StringUtils.isBlank(appId)) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid AppId");
		}
		mongoTemplate.insert(invitation, this.getCollectionName(appId));
		ensureIndexAtSentAt(getCollectionName(appId));
		
		return invitation;
	}
	
	public Invitation findInvitation(String appId, String senderUserId, String receiverUserId, 
			String groupId, String status) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(senderUserId) 
				|| StringUtils.isBlank(receiverUserId) || StringUtils.isBlank(groupId)
				|| StringUtils.isBlank(status)) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		Invitation invitation = null;
		
		Query query = new Query();
		Criteria[] criterias = {
				Criteria.where(DBKeys.SENDER_USER_ID).is(senderUserId),
				Criteria.where(DBKeys.RECEIVER_USER_ID).is(receiverUserId),
				Criteria.where(DBKeys.GROUP_ID).is(groupId),
				Criteria.where(DBKeys.STATUS).is(status)
		};
		Criteria criteria = new Criteria().andOperator(criterias);
		query.addCriteria(criteria);
		
		query.with(new Sort(Direction.DESC, DBKeys.SENT_AT));
		
		invitation = mongoTemplate.findOne(query, Invitation.class, this.getCollectionName(appId));
		return invitation;
	}
	
	
	public Invitation findInvitationByReceiverIdGroupIdStatus(String appId, String receiverUserId, String groupId, 
			String status) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(receiverUserId) 
				|| StringUtils.isBlank(groupId)) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		Invitation invitation = null;
		
		Query query = new Query();
		Criteria[] criterias = {
				Criteria.where(DBKeys.STATUS).is(status),
				Criteria.where(DBKeys.RECEIVER_USER_ID).is(receiverUserId),
				Criteria.where(DBKeys.GROUP_ID).is(groupId)
		};
		Criteria criteria = new Criteria().andOperator(criterias);
		query.addCriteria(criteria);
		
		query.with(new Sort(Direction.DESC, DBKeys.SENT_AT));
		
		invitation = mongoTemplate.findOne(query, Invitation.class, this.getCollectionName(appId));
		return invitation;
	}
	
	public Invitation findInvitationByInvitationIdReceiverId(String appId, String invitationId, 
			String receiverUserId) {
		if (StringUtils.isEmpty(invitationId) || StringUtils.isEmpty(appId) 
				|| StringUtils.isEmpty(receiverUserId)) {
			return null;
		}
		Query query = new Query();

		Criteria[] criterias = {
				Criteria.where(DBKeys.INVITATION_ID).is(invitationId),
				Criteria.where(DBKeys.RECEIVER_USER_ID).is(receiverUserId)
		};
		Criteria criteria = new Criteria().andOperator(criterias);
		query.addCriteria(criteria);
		
		return mongoTemplate.findOne(query, Invitation.class, getCollectionName(appId));
	}
	
	public void updateInvitation(String appId, Invitation invitation) { 
		if (invitation.getId() == null 
				|| StringUtils.isBlank(invitation.getInvitationId()) 
				|| StringUtils.isBlank(invitation.getGroupId())
				|| StringUtils.isBlank(invitation.getReceiverUserId())
				|| StringUtils.isBlank(invitation.getSenderUserId())
				|| StringUtils.isBlank(invitation.getStatus())
				|| StringUtils.isBlank(invitation.getMemberRole())) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		mongoTemplate.save(invitation, getCollectionName(appId));
	}
	
	private String getCollectionName(String appId) {
		return appId + DBKeys.INVITATION_COLLECTION_POSTFIX;
	}
	
	private void ensureIndexAtSentAt(String collectionName) {
		IndexDefinition indexDefinition = new Index(DBKeys.SENT_AT, Direction.DESC);
		mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
	}
}
