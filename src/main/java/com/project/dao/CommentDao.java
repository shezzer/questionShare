package com.project.dao;

import com.project.model.Comment;
import com.project.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Sherl on 2017/12/11.
 */
@Mapper
public interface CommentDao {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " userId, content, createdDate, entityId, entityType, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, "from ", TABLE_NAME, "where entityId=#{entityId} and entityType=#{entityType} order by id desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId,
                                         @Param("entityType") int entityType);

    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where id=#{id}"})
    Comment selectById(int id);

    //获取评论数
    @Select({"select count(id) from ", TABLE_NAME, " where entityId=#{entityId} and entityType=#{entityType} "})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " where userId=#{userId}"})
    int getUserCommentCount(int userId);

    //删除评论  更新评论状态
    @Update({"update ", TABLE_NAME, " set status=#{status} where entity_id=#{entityId} and entity_type=#{entityType}"})
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status);
}
