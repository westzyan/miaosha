package com.zyan.miaosha.vo;

import com.zyan.miaosha.domain.MiaoshaUser;

public class GoodsDetailVo {

    private int miaoshaStatus;
    private int remainSeconds;
    private GoodsVo goodsVo;
    private MiaoshaUser miaoshaUser;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public GoodsDetailVo(int miaoshaStatus, int remainSeconds, GoodsVo goodsVo, MiaoshaUser miaoshaUser) {
        this.miaoshaStatus = miaoshaStatus;
        this.remainSeconds = remainSeconds;
        this.goodsVo = goodsVo;
        this.miaoshaUser = miaoshaUser;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }


    public GoodsDetailVo() {
    }


}
