package com.project.controller;

import com.project.async.EventModel;
import com.project.async.EventProducer;
import com.project.async.EventType;
import com.project.model.Comment;
import com.project.model.EntityType;
import com.project.model.HostHolder;
import com.project.service.CommentService;
import com.project.service.LikeService;
import com.project.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Sherl on 2017/12/24.
 */
@Controller
public class LikeController {
    private static final Logger looger = LoggerFactory.getLogger(LikeController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUsers()==null){
            return WendaUtil.getJSONString(999);
        }
        Comment comment = commentService.getComment(commentId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE).
                setActorId(hostHolder.getUsers().getId()).
                setEntityId(commentId).
                setEntityType(EntityType.ENTITY_COMMENT).
                setEntityOwnerId(comment.getUserId()).
                setExt("questionId",String.valueOf(comment.getEntityId())));

        long likeCount = likeService.like(hostHolder.getUsers().getId(),commentId, EntityType.ENTITY_COMMENT);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }
    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUsers()==null){
            return WendaUtil.getJSONString(999);
        }
        long likeCount = likeService.dislike(hostHolder.getUsers().getId(),commentId, EntityType.ENTITY_COMMENT);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

}
