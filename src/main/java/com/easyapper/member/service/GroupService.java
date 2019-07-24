package com.easyapper.member.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.impl.ChainBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.easyapper.member.dao.GroupRepository;
import com.easyapper.member.dao.MemberRepository;
import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.ResponseMessage;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.operation.CommandContext;
import com.easyapper.member.operation.CommandOperationRequest;
import com.easyapper.member.service.command.AddGroupForMemberCommand;
import com.easyapper.member.service.command.CreateGroupCommand;
import com.easyapper.member.service.command.SendInvitationCommand;
import com.easyapper.member.service.command.ValidateGroupOperationRequestCommand;

@Service
public class GroupService {

    private final MemberRepository memberRepo;
    private final GroupRepository groupRepo;
    private final ApplicationContext appContext;

    @Autowired
    public GroupService(MemberRepository memberRepository, GroupRepository groupRepository, 
    		ApplicationContext appContext){
        this.memberRepo = memberRepository;
        this.groupRepo = groupRepository;
        this.appContext = appContext;
    }

//    public ObjectId createGroup(String appId, Group group) {
//        if(group.getGroupId() != null && group.getCreatedBy() != null &&
//                !group.getGroupId().isEmpty() ) {
//            Member member =  memberRepository.findMemberByUserId(appId, group.getCreatedBy());
//
//            if (member == null ) {
//                throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user does not exist");
//            }
//    
//            group.getMembers().add(member);
//            Group grp  = groupRepository.createGroup(appId, group);
//            if(grp != null){
//                for(Member aMember : group.getMembers()) {
//                    memberRepository.addToMemberGroups(appId, aMember, grp);
//                }
//                return grp.getId();
//            }
//        }
//        throw new MemberRuntimeException(ErrorCode.BAD_REQUEST,"mandatory fields missing");
//    }
    
//    @Transactional
    public ResponseMessage createGroup(String appId, String userId, Group group) throws Exception {
    		
		CommandOperationRequest contextRequest = new CommandOperationRequest();
		contextRequest.setAppId(appId);
		contextRequest.setUserId(userId);
		contextRequest.setGroup(group);
		contextRequest.setSendInviteGroupMembers( new ArrayList<>(group.getMembers()));

		//Execute Chain
		ChainBase chainBase = new ChainBase();
		chainBase.addCommand(appContext.getBean( ValidateGroupOperationRequestCommand.class ));
		chainBase.addCommand(appContext.getBean( CreateGroupCommand.class ));
		chainBase.addCommand(appContext.getBean( AddGroupForMemberCommand.class ));
		chainBase.addCommand(appContext.getBean( SendInvitationCommand.class ));
		
		CommandContext context = new CommandContext();
		context.setContextRequest(contextRequest);
		context.setContextResponse(new ResponseMessage());
		chainBase.execute(context);
			
		return context.getContextResponse();
    }

    public Group findGroup(String appId, String groupId) {
        Group grp = groupRepo.findGroup(appId, groupId);
        if(grp == null){
            throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "user group does not exist");
        }
        return grp;
    }

    public List<Member> findGroupMembers(String appId, String groupId) {
        Group grp = groupRepo.findGroup(appId, groupId);
        if(grp == null){
            throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "user group does not exist");
        }

        List<Member> grpMembers =  new ArrayList<>();

        for(Member member: grpMembers){
            if((member = memberRepo.findMemberByUserId(appId, member.getUserId())) != null){
                grpMembers.add(member);
            }
        }

        return grpMembers;
    }

    public int addGroupMembers(String appId, Group group, List<Member> members) {
        String groupId = group.getGroupId();
        Group grp = groupRepo.findGroup(appId, groupId);
        group.setMembers(null);

        if(grp == null){
            throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, "user group does not exist");
        }

        int size = 0;

        for(Member member : members) {
            if((member = memberRepo.findMemberByUserId(appId, member.getUserId())) != null) {
                member.setGroupIds(null);
                groupRepo.addGroupMember(appId, grp, member);
                memberRepo.addToMemberGroups(appId, member, group);
                size++;
            }
        }

        return size;
    }

    public List<Group> findGroupsByType(String appId) {
        return groupRepo.findGroupsByType(appId, "public");
    }
}
