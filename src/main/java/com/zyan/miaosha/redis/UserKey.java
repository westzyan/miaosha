package com.zyan.miaosha.redis;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-21 下午10:02
 */
public class UserKey extends BasePrefix {
    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");

}
