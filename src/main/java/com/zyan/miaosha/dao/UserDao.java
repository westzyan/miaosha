package com.zyan.miaosha.dao;

import com.zyan.miaosha.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-19 下午10:33
 */

@Mapper
@Component(value = "UserDao")
public interface UserDao {

    /**
     *
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    public User getById(@Param("id")int id);
}
