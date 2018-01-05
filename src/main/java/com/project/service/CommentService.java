package com.project.service;

import com.project.dao.CommentDao;
import com.project.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Sherl on 2017/12/20.
 */
@Service
public class CommentService {
    @Autowired
    CommentDao commentDao;

    public int addComment(Comment comment){
        return commentDao.addComment(comment);
    }

    public List<Comment> selectByEntity(int entityId,int entityType){
        return commentDao.selectCommentByEntity(entityId,entityType);
    }

    public Comment getComment(int id){
        return commentDao.selectById(id);
    }

    public int getUserCommentCount(int userId) {
        return commentDao.getUserCommentCount(userId);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDao.getCommentCount(entityId, entityType);
    }

    public void deleteComment(int entityId, int entityType) {
        commentDao.updateStatus(entityId, entityType, 1);
    }
}
