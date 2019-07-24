package com.easyapper.member.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.CounterEntity;


@Component
public class DBSeqFinder {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	private Long getNextSeqValue(String dbSequenceName) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(dbSequenceName));
		Update update = new Update();
		update.inc("seq", 1);
		CounterEntity counterEntity = null;
		try {
			counterEntity = mongoTemplate.findAndModify(query, update,
						CounterEntity.class);
			if (counterEntity == null ) {
				counterEntity = new CounterEntity(dbSequenceName, new Long(1001));
				mongoTemplate.insert(counterEntity);
				return counterEntity.getSeq();
			}
		}catch(Exception e) {
			throw new EAMemRuntimeException(ErrorCode.SERVER_ERROR);
		}
		return counterEntity.getSeq() + 1;
	}

	public String getNextValForGroup(String appId) {
		Long groupNextVal = this.getNextSeqValue(appId + GroupRepository.getGroupCollectionPostfix());
		return String.valueOf(groupNextVal);
	}
	
	public String getNextValForInvitation(String appId) {
		Long invitationNextVal =  this.getNextSeqValue(appId + InvitationRepository.getInvitationCollectionPostfix());
		return String.valueOf(invitationNextVal);
	}
	
}
