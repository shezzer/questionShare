package com.project.controller;

import com.project.model.Comment;
import com.project.model.EntityType;
import com.project.model.HostHolder;
import com.project.service.CommentService;
import com.project.service.QuestionService;
import com.project.service.SensitiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by Sherl on 2017/12/20.
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = "/addComment",method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setContent(sensitiveService.filter(content));
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            if(hostHolder.getUsers()!=null){
                comment.setUserId(hostHolder.getUsers().getId());
            }else{
                return "redirect:/reglogin";
            }
            comment.setStatus(0);
            commentService.addComment(comment);
            // 更新题目里的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);

        }catch (Exception e){
            logger.error("添加评论失败"+e.getMessage());
        }
        return "redirect:/question/" + String.valueOf(questionId);
    }
}
