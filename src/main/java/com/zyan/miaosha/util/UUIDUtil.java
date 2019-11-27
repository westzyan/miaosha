package com.zyan.miaosha.util;

import java.util.UUID;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-24 上午11:51
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
