package com.project.service;

import com.project.dao.QuestionDao;
import com.project.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by Sherl on 2017/12/13.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;
    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDao.selectLatestQuestions(userId,offset,limit);
    }

    public int addQuestion(Question question){
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        return questionDao.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public Question findById(int id){
        return questionDao.selectById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return questionDao.updateCommentCount(id,commentCount);
    }
}
