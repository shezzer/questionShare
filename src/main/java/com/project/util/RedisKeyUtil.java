package com.project.util;

/**
 * Created by Sherl on 2017/12/24.
 */
public class RedisKeyUtil {
    public static String SPILT = ":";
    public static String BIZ_LIKE = "like";
    public static String BIZ_DISLIKE = "dislike";
    public static String BIZ_EVENT_QUEUE = "EVENT_QUEUE";

    public static String BIZ_FOLLOWER = "FOLLOWER";
    public static String BIZ_FOLLOWEE = "FOLLOWEE";

    //某个实体的关注者key
    public static String getFollowerKey(int entityId,int entityType){
        return BIZ_FOLLOWER + SPILT + String.valueOf(entityType) + SPILT + String.valueOf(entityId);
    }
    //每个用户对某类实体的关注key
    public static String getFolloweeKey(int userId,int entityType){
        return BIZ_FOLLOWEE + SPILT + String.valueOf(userId) + SPILT + String.valueOf(entityType);
    }
    public static String getLikeKey(int entityId,int entityType){
        return BIZ_LIKE + SPILT + entityType + SPILT + entityId;
    }

    public static String getDislikeKey(int entityId,int entityType){
        return BIZ_DISLIKE + SPILT + entityType + SPILT + entityId;
    }
    public static String getEventQueue(){
        return BIZ_EVENT_QUEUE;
    }
}
