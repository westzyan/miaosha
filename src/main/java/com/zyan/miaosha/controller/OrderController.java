package com.zyan.miaosha.controller;

import com.zyan.miaosha.domain.Goods;
import com.zyan.miaosha.domain.MiaoshaUser;
import com.zyan.miaosha.domain.OrderInfo;
import com.zyan.miaosha.redis.RedisService;
import com.zyan.miaosha.result.CodeMsg;
import com.zyan.miaosha.result.Result;
import com.zyan.miaosha.service.GoodsService;
import com.zyan.miaosha.service.MiaoshaService;
import com.zyan.miaosha.service.MiaoshaUserService;
import com.zyan.miaosha.service.OrderService;
import com.zyan.miaosha.vo.GoodsVo;
import com.zyan.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zyan
 * @version 1.0
 * @date 19-12-02
 */

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	MiaoshaUserService userService;

	@Autowired
	RedisService redisService;

	@Autowired
	OrderService orderService;

	@Autowired
	GoodsService goodsService;


	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> info(Model model, MiaoshaUser user, @RequestParam("orderId") long orderId) {
		if (user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		OrderInfo orderInfo = orderService.getOrderById(orderId);
		if (orderInfo == null){
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		long goodsId = orderInfo.getGoodsId();
		GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo orderDetailVo = new OrderDetailVo();
		orderDetailVo.setGoodsVo(goodsVo);
		orderDetailVo.setOrderInfo(orderInfo);
		return Result.success(orderDetailVo);
	}

    
}
