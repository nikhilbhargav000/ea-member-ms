package com.easyapper.member.service;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.dao.MemberRepository;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.exception.MemberRuntimeException;
import com.easyapper.member.model.Group;
import com.easyapper.member.model.Member;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMgmntService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public UserMgmntService(MemberRepository memberRepository, GroupRepository groupRepository){
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }

    public ObjectId createUser(String appId, Member user) {
        if(user.getUserId() != null && user.getName()!= null &&
                !user.getUserId().isEmpty() && !user.getName().isEmpty()) {
            Member member = memberRepository.findMemberByUserId(appId, user.getUserId());
            if (member != null) {
                throw new MemberRuntimeException(ErrorCode.ALREADY_EXIST, "userId already used");
            }

            member = memberRepository.createMember(appId, user);
            if(member != null){
                return member.getId();
            }
        }
        throw new MemberRuntimeException(ErrorCode.BAD_REQUEST,"mandatory fields missing");
    }

    public List<Member> findUsers(String appId) {
        List<Member> members = memberRepository.findMembers(appId);
        if (members == null) {
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "userId not found");
        }

        return members;
    }

    public List<Group> findUserGroups(String appId, String userId) {
        Member member = memberRepository.findMemberByUserId(appId, userId);
        if (member == null) {
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "userId not found");
        }

        List<Group> groups =  new ArrayList<>();

        for(Group grp : member.getGroups()) {
//            if((grp = groupRepository.findGroup(appId, grp.getId())) != null){
//                groups.add(grp);
//            }
        }

        return groups;
    }

    public Member findMemberByUserId(String appId, String userId) {
        Member member = memberRepository.findMemberByUserId(appId,userId);
        return member;
    }
}
