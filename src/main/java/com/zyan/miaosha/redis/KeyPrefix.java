package com.zyan.miaosha.redis;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-21 下午8:49
 */
public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();

}
