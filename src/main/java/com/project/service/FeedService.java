package com.project.service;

import com.project.dao.FeedDao;
import com.project.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Sherl on 2017/12/30.
 */
@Service
public class FeedService {
    @Autowired
    FeedDao feedDao;

    public boolean addFeed(Feed feed){
        return feedDao.addFeed(feed) > 0;
    }

    //拉模式，直接在用户登录浏览时展示
    public Feed getFeedById(int id){
        return feedDao.getFeedById(id);
    }

    public List<Feed> selectUserFeeds(List<Integer> userIds,int maxId,int count){
        return feedDao.selectUserFeeds(userIds, maxId,count);
    }
}
