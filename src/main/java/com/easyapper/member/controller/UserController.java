package com.easyapper.member.controller;

import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.exception.MemberRuntimeException;
import com.easyapper.member.model.Group;
import com.easyapper.member.model.Member;
import com.easyapper.member.model.ResponseMessage;
import com.easyapper.member.service.GroupMgmntService;
import com.easyapper.member.service.UserMgmntService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class UserController {
    private final UserMgmntService userSrv;
    private final GroupMgmntService groupService;

    @Autowired
    public UserController(UserMgmntService userSrv, GroupMgmntService groupService){
        this.userSrv = userSrv;
        this.groupService = groupService;
    }

    @RequestMapping(value= "/{appId}/users", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> registerUser(@PathVariable("appId") String appId,
                                                        @RequestBody Member user){
        ResponseMessage resMsg = new ResponseMessage();
        HttpStatus status = HttpStatus.CREATED;
        if (user.getUserId() == null || user.getUserId().isEmpty() ||	//For Validation
                user.getName() == null || user.getName().isEmpty()){
            resMsg.setStatus("Failed");
            resMsg.setMessage("Mandatory field missing");
            status = HttpStatus.BAD_REQUEST;
        } else if (userSrv.findMemberByUserId(appId, user.getUserId()) == null) {
            ObjectId id = userSrv.createUser(appId, user);

            resMsg.setId(id.toHexString());
            resMsg.setStatus("Success");
            resMsg.setMessage("User created successfully");
        } else {
            resMsg.setStatus("Failed");
            resMsg.setMessage("userId already taken");
            status = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(resMsg, status);
    }
    

    @RequestMapping(value= "/{appId}/users/{userId}", method = RequestMethod.GET)
    public Member findMember(@PathVariable("appId") String appId, @PathVariable("userId") String userId){
        Member member = userSrv.findMemberByUserId(appId, userId);
        if(member == null) {
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user does not exist");
        }
        //member.setGroups(null);
        return member;
    }

    @RequestMapping(value= "/{appId}/users", method = RequestMethod.GET)
    public List<Member> findMembers(@PathVariable("appId") String appId){
        return userSrv.findUsers(appId);
    }
    
    
    
    
    
    
    @RequestMapping(value= "/{appId}/users/{userId}/groups", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> registerGroup(@PathVariable("appId") String appId,
                                                         @PathVariable("userId") String userId,
                                                         @RequestBody Group group){
        ResponseMessage resMsg = new ResponseMessage();
        HttpStatus status = HttpStatus.CREATED;

        if( group.getGroupId() == null || group.getGroupId().isEmpty() ||
                userSrv.findMemberByUserId(appId, userId) == null || group.getCreatedBy() == null ||
                (!"public".equals(group.getType())&& !"private".equals(group.getType()))){
            resMsg.setStatus("Failed");
            resMsg.setMessage("Mandatory field missing or invalid");
            status = HttpStatus.BAD_REQUEST;
        }else {
            ObjectId id = groupService.createGroup(appId, group);
            resMsg.setId(id.toHexString());
            resMsg.setStatus("Success");
            resMsg.setMessage("User Group created successfully");
        }

        return new ResponseEntity<>(resMsg, status);
    }

    @RequestMapping(value= "/{appId}/users/{userId}/groups/{groupId}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseMessage> addGroupMembers(@PathVariable("appId") String appId,
                                                           @PathVariable("groupId") String groupId,
                                                           @RequestBody List<Member> members){
        ResponseMessage resMsg = new ResponseMessage();
        HttpStatus status = HttpStatus.OK;

        Group grp = groupService.findGroup(appId, groupId);
        if(grp == null || !grp.getGroupId().equals(grp.getGroupId())){
            resMsg.setStatus("Failed");
            resMsg.setMessage("Mandatory field missing or invalid");
            status = HttpStatus.BAD_REQUEST;
        }else{
            groupService.addGroupMembers(appId, grp, members);
            resMsg.setId(null);
            resMsg.setStatus("Success");
            resMsg.setMessage("Group members added successfully");
        }

        return new ResponseEntity<>(resMsg, status);
    }


    @RequestMapping(value= "/{appId}/users/{userId}/groups", method = RequestMethod.GET)
    public List<Group> findUserGroups(@PathVariable("appId") String appId, @PathVariable("userId") String userId){
        Set<Group> groups = new HashSet<>();
        if( userSrv.findMemberByUserId(appId, userId) == null){
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user does not exist");
        }else{
            groups.addAll(userSrv.findUserGroups(appId, userId));
        }

        groups.addAll(groupService.findGroupsByType("public"));

        return new ArrayList<>(groups);
    }

    @RequestMapping(value= "/{appId}/users/{userId}/groups/{groupId}", method = RequestMethod.GET)
    public Group findUserGroup(@PathVariable("appId") String appId, @PathVariable("userId") String userId,
              @PathVariable("groupId") String groupId){
        if( userSrv.findMemberByUserId(appId, userId) == null){
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, "user does not exist");
        }else{
            return groupService.findGroup(appId, groupId);
        }
    }
}
