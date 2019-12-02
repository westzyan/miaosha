package com.zyan.miaosha.redis;

/**
 * @author zyan
 * @version 1.0
 * @date 19-12-2 上午11:22
 */
public class GoodsKey extends BasePrefix{

    public GoodsKey(int expire, String prefix) {
        super(expire,prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
}
