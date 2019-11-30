package com.zyan.miaosha.controller;

import com.zyan.miaosha.domain.MiaoshaOrder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-24 下午3:59
 */

@Controller
@RequestMapping("/user")
public class UserController {

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

	@RequestMapping("/info")
	@ResponseBody
	public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
		model.addAttribute("user", user);

		return Result.success(user);
	}

    
}
