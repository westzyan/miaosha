package com.zyan.miaosha.service;

import com.zyan.miaosha.domain.Goods;
import com.zyan.miaosha.domain.MiaoshaUser;
import com.zyan.miaosha.domain.OrderInfo;
import com.zyan.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-28 下午10:57
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        //减库存，写订单，写入秒杀订单

        goodsService.reduceStock(goodsVo);

        //order_info miaosha_order
        return orderService.createOrder(user, goodsVo);

    }
}
