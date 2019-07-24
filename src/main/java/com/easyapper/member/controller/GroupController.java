package com.easyapper.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.easyapper.member.exception.EAMemRuntimeException;
import com.easyapper.member.exception.ErrorCode;
import com.easyapper.member.model.group.Group;
import com.easyapper.member.service.GroupService;
import com.easyapper.member.service.UserService;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class GroupController {

    private final UserService userSrv;
    private final GroupService groupService;

    @Autowired
    public GroupController(UserService userSrv, GroupService groupService){
        this.userSrv = userSrv;
        this.groupService = groupService;
    }

    @RequestMapping(value= "/{appId}/groups/{groupId}", method = RequestMethod.GET)
    public Group findGroup(@PathVariable("appId") String appId, @PathVariable("groupId") String groupId){
        Group group = groupService.findGroup(appId, groupId);
        if(group == null){
            throw new EAMemRuntimeException(ErrorCode.NOT_FOUND, " group does not exist");
        }
        //group.setMembers(null);
        return group;
    }


    @RequestMapping(value= "/{appId}/groups", method = RequestMethod.GET)
    public List<Group> findOpenGroups(@PathVariable("appId") String appId){

        return groupService.findGroupsByType(appId);
    }

}
