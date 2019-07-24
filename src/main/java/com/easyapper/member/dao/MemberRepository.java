package com.easyapper.member.dao;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.util.EAMemConstants;

@Repository
public class MemberRepository {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public MemberRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public static class DBKeys {
		public static final String ID = "id";
		public static final String GROUP_IDS = "groupIds";
		public static final String IS_ACTIVE = "isActive";
		public static final String NAME = "name";
		public static final String USER_ID = "userId";
		public static final String MEMBER_COLLECTION_POSTFIX = "_members";
	}

	public List<Member> findMembers(String appId, Map<String, String> paramMap) {
		Query aQuery = new Query();

		if (paramMap.containsKey(EAMemConstants.MEMBER_NAME_QUERY_PARAM)
				&& StringUtils.isNotBlank(paramMap.get(EAMemConstants.MEMBER_NAME_QUERY_PARAM))) {
			String value = paramMap.get(EAMemConstants.MEMBER_NAME_QUERY_PARAM);
			Pattern alikeCaseInsentitvePattern = Pattern.compile(Pattern.quote(value.trim()), Pattern.CASE_INSENSITIVE);
			Criteria[] criterias = { Criteria.where(DBKeys.IS_ACTIVE).is(true),
					Criteria.where(DBKeys.NAME).regex(alikeCaseInsentitvePattern) };
			Criteria criteria = new Criteria().andOperator(criterias);
			aQuery.addCriteria(criteria);
		} else {
			aQuery.addCriteria(Criteria.where(DBKeys.IS_ACTIVE).is(true));
		}

		return mongoTemplate.find(aQuery, Member.class, getCollectionName(appId));
	}

	public Member findMemberByUserId(String appId, String userId) {
		if (appId == null || appId.isEmpty() || userId == null || userId.isEmpty()) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}

		Query aQuery = new Query();
		aQuery.addCriteria(Criteria.where(DBKeys.USER_ID).is(userId).and(DBKeys.IS_ACTIVE).is(true));

		return mongoTemplate.findOne(aQuery, Member.class, getCollectionName(appId));
	}

	public Member createMember(String appId, Member user) {
		if (appId == null || appId.isEmpty() || user == null || user.getUserId() == null || user.getUserId().isEmpty()
				|| user.getName() == null || user.getName().isEmpty()) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		if (findMemberByUserId(appId, user.getUserId()) == null) {
			user.setUserId(user.getUserId());
			user.setActive(true);
			user.setCreatedAt(Instant.now().getEpochSecond());
			user.setGroupIds(new HashSet<>());
			mongoTemplate.insert(user, getCollectionName(appId));
			return user;
		}
		throw new EAMemRuntimeException(ErrorCode.ALREADY_EXIST);
	}

	public void updateMember(String appId, Member member) {
		if (member == null || member.getId() == null || member.getGroupIds() == null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}
		mongoTemplate.save(member, getCollectionName(appId));
	}
	
	public void updateMemberGroups(String appId, Member member) {
		if (member == null || member.getId() == null || member.getGroupIds() == null) {
			throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST);
		}

		Member aMember = this.findMemberByUserId(appId, member.getUserId());
		aMember.getGroupIds().addAll(member.getGroupIds());

		if (aMember != null) {
			Query query = new Query();
			query.addCriteria(Criteria.where(DBKeys.ID).is(member.getId()).and(DBKeys.IS_ACTIVE).is(true));

			Update update = new Update();
			update.set(DBKeys.GROUP_IDS, aMember.getGroupIds());
			mongoTemplate.updateFirst(query, update, Member.class, getCollectionName(appId));
		}
	}

	public void addToMemberGroups(String appId, Member member, Group grp) {
		if (member != null) {
			Query query = new Query();
			query.addCriteria(Criteria.where(DBKeys.ID).is(member.getId()).and(DBKeys.IS_ACTIVE).is(true));

			Update update = new Update();
			update.push(DBKeys.GROUP_IDS, grp);
			mongoTemplate.updateFirst(query, update, Member.class, getCollectionName(appId));
		}
	}
	
	public String getCollectionName(String appId) {
		if (appId == null) {
			return null;
		}
		return appId + DBKeys.MEMBER_COLLECTION_POSTFIX;
	}
}
