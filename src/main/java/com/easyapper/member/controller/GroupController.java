package com.easyapper.member.controller;

import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.exception.MemberRuntimeException;
import com.easyapper.member.model.Group;
import com.easyapper.member.model.Member;
import com.easyapper.member.service.GroupMgmntService;
import com.easyapper.member.service.UserMgmntService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class GroupController {

    private final UserMgmntService userSrv;
    private final GroupMgmntService groupService;

    @Autowired
    public GroupController(UserMgmntService userSrv, GroupMgmntService groupService){
        this.userSrv = userSrv;
        this.groupService = groupService;
    }

    @RequestMapping(value= "/{appId}/groups/{groupId}", method = RequestMethod.GET)
    public Group findGroup(@PathVariable("appId") String appId, @PathVariable("groupId") String groupId){
        Group group = groupService.findGroup(appId, groupId);
        if(group == null){
            throw new MemberRuntimeException(ErrorCode.NOT_FOUND, " group does not exist");
        }
        //group.setMembers(null);
        return group;
    }


    @RequestMapping(value= "/{appId}/groups", method = RequestMethod.GET)
    public List<Group> findOpenGroups(@PathVariable("appId") String appId){

        return groupService.findGroupsByType(appId);
    }

}
