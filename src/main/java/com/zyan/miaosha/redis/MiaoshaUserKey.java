package com.zyan.miaosha.redis;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-24 上午11:54
 */
public class MiaoshaUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 3600*24*2;
    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");
    public static MiaoshaUserKey getByid = new MiaoshaUserKey(0,"id");

}
