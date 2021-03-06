package com.easyapper.member.dao;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.group.Group;

@Repository
public class GroupRepository {

    private static final String GROUP_COLLECTION_POSTFIX = "_groups";
	
    private static final Logger log = LoggerFactory.getLogger(GroupRepository.class);

    private final MongoOperations mongoTemplate;
    
    @Autowired
    public GroupRepository(MongoOperations mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public Group findGroup(String appId, String id) {
        if(id == null){
            return null;
        }
        Query aQuery = new Query();
        aQuery.addCriteria(Criteria.where("groupId").is(id).and("isActive").is(true));

        return mongoTemplate.findOne(aQuery, Group.class, appId + GROUP_COLLECTION_POSTFIX);
    }

    public Group createGroup(String appId, Group group) {
        if(appId == null || appId.isEmpty() || group == null ||
                group.getGroupId()== null || group.getGroupId().isEmpty() ||
                group.getCreatedBy() == null ){
            throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
        }

        group.setActive(true);
        group.setCreatedAt(Instant.now().getEpochSecond());

        mongoTemplate.insert(group, appId + GROUP_COLLECTION_POSTFIX);
        return group;
    }

    public void updateGroupMembers(String appId, Group group) {
        if( group == null || group.getId() == null || group.getMembers()== null  ){
            throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
        }

        Group aGroup = findGroup(appId, group.getGroupId());
        if( aGroup != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(group.getId()).and("isActive").is(true));

            Update update = new Update();
            update.set("members", group.getMembers());
            mongoTemplate.updateFirst(query, update, Group.class, appId + GROUP_COLLECTION_POSTFIX);
        }
    }

    public void addGroupMember(String appId, Group group, Member member) {
        if( group == null || group.getId() == null || group.getMembers()== null  ){
            throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
        }

        Group aGroup = findGroup(appId, group.getGroupId());
        if( aGroup != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(group.getId()).and("isActive").is(true));

            Update update = new Update();
            update.push("members", member);
            mongoTemplate.updateFirst(query, update, Group.class, appId + GROUP_COLLECTION_POSTFIX);
        }
    }

    public List<Group> findGroupsByType(String appId, String grpType) {
        Query aQuery = new Query();
        aQuery.addCriteria(Criteria.where("type").is(grpType).and("isActive").is(true));

        return mongoTemplate.find(aQuery, Group.class, appId + GROUP_COLLECTION_POSTFIX);
    }
    
    public static String getGroupCollectionPostfix() {
    	return GROUP_COLLECTION_POSTFIX;
    }
    
}
