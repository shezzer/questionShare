package com.project.dao;

import com.project.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Created by Sherl on 2017/12/11.
 */
@Mapper
public interface UserDao {
    // 注意空格
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " name, password, salt, headUrl ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    //添加用户
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    //根据Id查找用户
    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where id=#{id}"})
    User selectById(int id);

    //根据name查找用户
    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where name=#{name}"})
    User selectByUserName(String username);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById(int id);
}
