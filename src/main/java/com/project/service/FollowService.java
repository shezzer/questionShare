package com.project.service;

import com.project.util.JedisAdapter;
import com.project.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Created by Sherl on 2017/12/27.
 */
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //两个操作同时进行，粉丝列表加入用户，用户关注列表加入关注的实体
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size()==2 && (long)ret.get(0)>0 && (long)ret.get(1)>0;
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //两个操作同时进行，粉丝列表中删除用户，用户关注列表删除原来关注的实体
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size()==2 && (long)ret.get(0)>0 && (long)ret.get(1)>0;
    }

    private List<Integer> getIdsList(Set<String> idSet){
        List<Integer> list = new ArrayList<>();
        for(String str : idSet){
            list.add(Integer.parseInt(str));
        }
        return list;
    }

    //得到粉丝列表
    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        return getIdsList(jedisAdapter.zrevrange(followerKey,0,count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        return getIdsList(jedisAdapter.zrevrange(followerKey,offset,offset+count));
    }
    //得到用户关注列表
    public List<Integer> getFollowees(int userId, int entityType, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsList(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    public List<Integer> getFollowees(int userId, int entityType, int offset, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsList(jedisAdapter.zrevrange(followeeKey,offset,offset+count));
    }
    //得到粉丝数
    public long getFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        return jedisAdapter.zcard(followerKey);
    }
    //得到关注数
    public long getFolloweeCount(int entityType, int userId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    public boolean isFollower(int userId, int entityId, int entityType){
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!= null;
    }
}
