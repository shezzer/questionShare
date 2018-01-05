package com.project.controller;

import com.project.model.*;
import com.project.service.CommentService;
import com.project.service.FollowService;
import com.project.service.QuestionService;
import com.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sherl on 2017/12/12.
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/index","/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,@RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getQuestionList(0, 0, 10));
        return "index";
    }

    private List<ViewObject> getQuestionList(int userId,int offset,int limit){
        List<ViewObject> vos = new ArrayList<>();
        List<Question> questionList = questionService.getLatestQuestions(userId,offset,limit);
        for(Question question : questionList){
            ViewObject vo = new ViewObject();
            vo.setObs("question",question);
            vo.setObs("user",userService.getUser(question.getUserId()));
            vo.setObs("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String Userindex(Model model,@PathVariable ("userId") int userId) {
        model.addAttribute("vos", getQuestionList(userId, 0, 10));
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.setObs("user", user);
        vo.setObs("commentCount", commentService.getUserCommentCount(userId));
        vo.setObs("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.setObs("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUsers() != null) {
            vo.setObs("followed", followService.isFollower(hostHolder.getUsers().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.setObs("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }
}
