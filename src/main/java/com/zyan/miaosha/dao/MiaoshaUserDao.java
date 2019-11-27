package com.zyan.miaosha.dao;

import com.zyan.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-23 下午3:59
 */
@Mapper
@Component(value = "MiaoshaUserDao")
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getById(@Param("id")long id);
}

