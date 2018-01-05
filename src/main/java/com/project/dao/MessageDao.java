package com.project.dao;

import com.project.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Sherl on 2017/12/19.
 */
@Mapper
public interface MessageDao {
    String TABLE_NAME = " message ";
    String INSERT_FIELD = "fromId, toId, createdDate, hasRead, content, conversationId";
    String SELECT_FIELD = " id," + INSERT_FIELD;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELD, ") values (#{fromId},#{toId},#{createdDate},#{hasRead},#{content},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELD, "from ", TABLE_NAME, "where conversationId=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,@Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME, "where hasRead=0 and toId=#{userId} and conversationId=#{conversationId}"})
    int getConvesationUnreadCount(@Param("userId") int userId,@Param("conversationId") String conversationId);

    @Select({"select ", INSERT_FIELD, " ,count(id) as id from ( select * from ", TABLE_NAME, " where fromId=#{userId} or toId=#{userId} order by id desc) tt group by conversationId  order by createdDate desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,@Param("offset") int offset, @Param("limit") int limit);

}
