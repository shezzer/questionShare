package com.project.controller;

import com.project.async.EventModel;
import com.project.async.EventProducer;
import com.project.async.EventType;
import com.project.model.*;
import com.project.service.*;
import com.project.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by Sherl on 2017/12/17.
 */
@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/followUser",method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.follow(hostHolder.getUsers().getId(),EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).
                setActorId(hostHolder.getUsers().getId()).
                setEntityType(EntityType.ENTITY_USER).
                setEntityId(userId).
                setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret ? 0 : 1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUsers().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(value = "/unfollowUser",method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUsers().getId(),EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(hostHolder.getUsers().getId()).
                setEntityType(EntityType.ENTITY_USER).
                setEntityId(userId).
                setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret ? 0 : 1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUsers().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(value = "/followQuestion",method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionService.findById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followService.follow(hostHolder.getUsers().getId(),EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUsers().getId()).
                setEntityType(EntityType.ENTITY_QUESTION).
                setEntityId(questionId).
                setEntityOwnerId(q.getUserId()));
        Map<String,Object> info = new HashMap<>();
        info.put("headUrl",hostHolder.getUsers().getHeadUrl());
        info.put("name",hostHolder.getUsers().getName());
        info.put("id",hostHolder.getUsers().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));

        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(value = "/unfollowQuestion",method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionService.findById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followService.unfollow(hostHolder.getUsers().getId(),EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(hostHolder.getUsers().getId()).
                setEntityType(EntityType.ENTITY_QUESTION).
                setEntityId(questionId).
                setEntityOwnerId(q.getUserId()));
        Map<String,Object> info = new HashMap<>();
        info.put("headUrl",hostHolder.getUsers().getHeadUrl());
        info.put("name",hostHolder.getUsers().getName());
        info.put("id",hostHolder.getUsers().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));

        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"user/{uid}/followees"},method = RequestMethod.GET)
    public String followees(@PathVariable("uid") int userId,Model model){
        List<Integer> followeeIds = followService.getFollowees(userId,EntityType.ENTITY_USER, 0, 10);
        if (hostHolder.getUsers() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUsers().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount( EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    @RequestMapping(path = {"user/{uid}/followers"},method = RequestMethod.GET)
    public String followers(@PathVariable("uid") int userId,Model model){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUsers() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUsers().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    private List<ViewObject> getUsersInfo(int localUserId,List<Integer> userIds){
        List<ViewObject> vos = new ArrayList<>();
        for(Integer uid : userIds){
            ViewObject vo = new ViewObject();
            User user = userService.getUser(uid);
            if(user == null){
                continue;
            }
            vo.setObs("user",user);
            vo.setObs("commentCount", commentService.getUserCommentCount(uid));
            vo.setObs("followeeCount",followService.getFolloweeCount(EntityType.ENTITY_USER,uid));
            vo.setObs("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,uid));
            if(localUserId != 0){
                vo.setObs("followed",followService.isFollower(localUserId,uid,EntityType.ENTITY_USER));
            }else {
                vo.setObs("followed",false);
            }
            vos.add(vo);
        }
        return vos;
    }
}
