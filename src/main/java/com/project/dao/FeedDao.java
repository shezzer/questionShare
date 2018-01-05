package com.project.dao;

import com.project.model.Comment;
import com.project.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Sherl on 2017/12/11.
 */
@Mapper
public interface FeedDao {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " userId, createdDate, type, data ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{createdDate},#{type},#{data})"})
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("userIds") List<Integer> userIds,
                               @Param("maxId") int maxId,
                               @Param("count") int count);
}
