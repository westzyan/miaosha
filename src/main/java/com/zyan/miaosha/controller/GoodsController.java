package com.zyan.miaosha.controller;

import com.zyan.miaosha.domain.MiaoshaOrder;
import com.zyan.miaosha.domain.OrderInfo;
import com.zyan.miaosha.result.CodeMsg;
import com.zyan.miaosha.service.GoodsService;
import com.zyan.miaosha.service.MiaoshaService;
import com.zyan.miaosha.service.OrderService;
import com.zyan.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zyan.miaosha.domain.MiaoshaUser;
import com.zyan.miaosha.redis.RedisService;
import com.zyan.miaosha.service.MiaoshaUserService;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-24 下午3:59
 */

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;

	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;

	@RequestMapping("/to_list")
	public String list(Model model,MiaoshaUser user) {
		model.addAttribute("user", user);
		//查询商品列表
		List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsVoList);
		return "goods_list";
	}

	@RequestMapping("/to_detail")
	public String detail(Model model, MiaoshaUser user, @PathVariable("goodsId")long goodsId) {

		//snowflake 算法
		model.addAttribute("user", user);
		//查询商品
		GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goodsVo);

		//
		long startAt = goodsVo.getStartDate().getTime();
		long endAt = goodsVo.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		//秒杀没有开始，倒计时
		if (now < startAt){
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now)/1000);
		}else if (now > endAt){
			miaoshaStatus = 2;
			miaoshaStatus = -1;
		}else {
			miaoshaStatus = 1;
		}

		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);

		return "goods_list";
	}


	@RequestMapping("/do_miaosha")
	public String list(Model model, MiaoshaUser user,
					   @RequestParam("goodsId")long goodsId) {
		model.addAttribute("user", user);
		if (user == null){
			return "login";
		}
		//判断库存
		GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goodsVo.getStockCount();
		if (stock <= 0){
			model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER.getMsg());
			return "miaosha_fail";
		}
		//判断是否已经秒杀到
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsVo.getId());
		if (miaoshaOrder != null){
			model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
			return "miaosha_fail";
		}
		//减库存，写订单，写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
		model.addAttribute("orderInfo", orderInfo);
		model.addAttribute("goods", goodsVo);
		return "order_detail";
	}
    
}
