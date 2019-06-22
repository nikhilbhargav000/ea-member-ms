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
public class GroupMgmntService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public GroupMgmntService(MemberRepository memberRepository, GroupRepository groupRepository){
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }

    public ObjectId createGroup(String appId, Group group) {
        if(group.getGroupId() != null && group.getCreatedBy() != null &&
                !group.getGroupId().isEmpty() ) {
            Member member =  memberRepository.findMemberByUserId(appId, group.getCreatedBy());

            if (member == null ) {
                throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user does not exist");
            }


            group.getMembers().add(member);
            Group grp  = groupRepository.createGroup(appId, group);
            if(grp != null){
                for(Member aMember : group.getMembers()) {
                    memberRepository.addToMemberGroups(appId, aMember, grp);
                }
                return grp.getId();
            }
        }
        throw new MemberRuntimeException(ErrorCode.BAD_REQUEST,"mandatory fields missing");
    }

    public Group findGroup(String appId, String groupId) {
        Group grp = groupRepository.findGroup(appId, groupId);
        if(grp == null){
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user group does not exist");
        }
        return grp;
    }

    public List<Member> findGroupMembers(String appId, String groupId) {
        Group grp = groupRepository.findGroup(appId, groupId);
        if(grp == null){
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user group does not exist");
        }

        List<Member> grpMembers =  new ArrayList<>();

        for(Member member: grpMembers){
            if((member = memberRepository.findMemberByUserId(appId, member.getUserId())) != null){
                grpMembers.add(member);
            }
        }

        return grpMembers;
    }

    public int addGroupMembers(String appId, Group group, List<Member> members) {
        String groupId = group.getGroupId();
        Group grp = groupRepository.findGroup(appId, groupId);
        group.setMembers(null);

        if(grp == null){
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user group does not exist");
        }

        int size = 0;

        for(Member member : members) {
            if((member = memberRepository.findMemberByUserId(appId, member.getUserId())) != null) {
                member.setGroups(null);
                groupRepository.addGroupMember(appId, grp, member);
                memberRepository.addToMemberGroups(appId, member, group);
                size++;
            }
        }

        return size;
    }

    public List<Group> findGroupsByType(String appId) {
        return groupRepository.findGroupsByType(appId, "public");
    }
}
