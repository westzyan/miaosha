package com.zyan.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zyan.miaosha.domain.MiaoshaUser;
import com.zyan.miaosha.redis.RedisService;
import com.zyan.miaosha.service.MiaoshaUserService;

import javax.servlet.http.HttpServletResponse;

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

	@RequestMapping("/to_list")
	public String list(Model model,MiaoshaUser user) {
		model.addAttribute("user", user);
		return "goods_list";
	}
    
}
