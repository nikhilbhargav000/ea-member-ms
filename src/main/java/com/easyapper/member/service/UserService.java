package com.easyapper.member.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.dao.MemberRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.group.Group;

@Service
public class UserService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public UserService(MemberRepository memberRepository, GroupRepository groupRepository){
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }

    public ObjectId createUser(String appId, Member user) {
        if(user.getUserId() != null && user.getName()!= null &&
                !user.getUserId().isEmpty() && !user.getName().isEmpty()) {
            Member member = memberRepository.findMemberByUserId(appId, user.getUserId());
            if (member != null) {
                throw new EAMemRuntimeException(ErrorCode.ALREADY_EXIST, "userId already used");
            }

            member = memberRepository.createMember(appId, user);
            if(member != null){
                return member.getId();
            }
        }
        throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST,"mandatory fields missing");
    }

    public List<Member> findUsers(String appId, Map<String, String> paramMap) {
        List<Member> members = memberRepository.findMembers(appId, paramMap);
        if (members == null) {
            throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "userId not found");
        }

        return members;
    }
    
    public List<Group> findUserGroups(String appId, String userId) {
    	
        Member member = memberRepository.findMemberByUserId(appId, userId);
        if (member == null) {
            throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "userId not found");
		}
//        return member.getGroupIds();
        List<Group> groups =  new ArrayList<>();
        Group group = null;
        for(String groupId : member.getGroupIds()) {
        	group = groupRepository.findGroup(appId, groupId);
            if(group != null){
                groups.add(group);
            }
        }
        return groups;
    }
    	
    public Member findMemberByUserId(String appId, String userId) {
        Member member = memberRepository.findMemberByUserId(appId,userId);
        return member;
    }
    
//    private boolean isGroupPublic(String appId, String groupId) {
//    	Group group = groupRepository.findGroup(appId, groupId);
//    	if (group == null) {
//    		throw new EAMemRuntimeException(ErrorCode.BAD_REQUEST, "Invalid groupId");
//    	}
//    	if (group.getType().equals(GroupType.PUBLIC.getValue())) {
//    		return true;
//    	}
//    	return false;
//    }
}
