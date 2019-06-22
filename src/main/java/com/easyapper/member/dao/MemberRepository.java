package com.easyapper.member.dao;

import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.exception.MemberRuntimeException;
import com.easyapper.member.model.Group;
import com.easyapper.member.model.Member;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class MemberRepository {
    private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);

    private final MongoOperations mongoTemplate;

    @Autowired
    public MemberRepository(MongoOperations mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public List<Member> findMembers(String appId) {
        Query aQuery = new Query();
        aQuery.addCriteria(Criteria.where("isActive").is(true));

        return mongoTemplate.find(aQuery, Member.class, appId+"_members");
    }

    public Member findMemberByUserId(String appId, String userId) {
        if(appId == null || appId.isEmpty() || userId== null|| userId.isEmpty()){
            throw new MemberRuntimeException(ErrorCode.BAD_REQUEST);
        }

        Query aQuery = new Query();
        aQuery.addCriteria(Criteria.where("userId").is(userId).and("isActive").is(true));

        return mongoTemplate.findOne(aQuery, Member.class, appId+"_members");
    }

    public Member createMember(String appId, Member user) {
        if(appId == null || appId.isEmpty() || user == null ||
                user.getUserId()== null || user.getUserId().isEmpty() ||
                user.getName() == null || user.getName().isEmpty()){
            throw new MemberRuntimeException(ErrorCode.BAD_REQUEST);
        }
        if(findMemberByUserId(appId, user.getUserId()) == null) {
            user.setUserId(user.getUserId());
            user.setActive(true);
            user.setCreatedAt(Instant.now().getEpochSecond());

            mongoTemplate.insert(user, appId+"_members");
            return user;
        }
        throw new MemberRuntimeException(ErrorCode.ALREADY_EXIST);
    }

    public void updateMemberGroups(String appId, Member member) {
        if( member == null || member.getId() == null || member.getGroups()== null  ){
            throw new MemberRuntimeException(ErrorCode.BAD_REQUEST);
        }

        Member aMember = findMemberByUserId(appId, member.getUserId());
        aMember.getGroups().addAll(member.getGroups());

        if( aMember != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(member.getId()).and("isActive").is(true));

            Update update = new Update();
            update.set("groups", aMember.getGroups());
            mongoTemplate.updateFirst(query, update, Member.class, appId+"_members");
        }
    }

    public void addToMemberGroups(String appId, Member member, Group grp) {
        if( member != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(member.getId()).and("isActive").is(true));

            Update update = new Update();
            update.push("groups", grp);
            mongoTemplate.updateFirst(query, update, Member.class, appId+"_members");
        }
    }
}
