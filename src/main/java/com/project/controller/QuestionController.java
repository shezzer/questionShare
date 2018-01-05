package com.project.controller;

import com.project.model.*;
import com.project.service.CommentService;
import com.project.service.LikeService;
import com.project.service.QuestionService;
import com.project.service.UserService;
import com.project.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sherl on 2017/12/17.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setCommentCount(0);
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            if(hostHolder.getUsers() == null){
                return WendaUtil.getJSONString(999);
            }else {
                question.setUserId(hostHolder.getUsers().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }
        }catch (Exception e){
            logger.error("msg",e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }

    @RequestMapping(value = "/question/{questionId}")
    public String addQuestion(@PathVariable("questionId") int questionId, Model model){
        Question question = questionService.findById(questionId);
        model.addAttribute("question", question);
        List<Comment> commentList = commentService.selectByEntity(questionId, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for(Comment comment : commentList){
            ViewObject vo = new ViewObject();
            vo.setObs("comment",comment);
            if(hostHolder.getUsers()==null)
                vo.setObs("liked",0);
            else {
                vo.setObs("liked",likeService.getLikeStatus(hostHolder.getUsers().getId(),comment.getId(),EntityType.ENTITY_COMMENT));
            }
            vo.setObs("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            vo.setObs("user",userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments",vos);

        return "detail";
    }
}
