package com.zyan.miaosha.controller;

import com.zyan.miaosha.domain.MiaoshaOrder;
import com.zyan.miaosha.domain.OrderInfo;
import com.zyan.miaosha.redis.GoodsKey;
import com.zyan.miaosha.result.CodeMsg;
import com.zyan.miaosha.result.Result;
import com.zyan.miaosha.service.GoodsService;
import com.zyan.miaosha.service.MiaoshaService;
import com.zyan.miaosha.service.OrderService;
import com.zyan.miaosha.vo.GoodsDetailVo;
import com.zyan.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.zyan.miaosha.domain.MiaoshaUser;
import com.zyan.miaosha.redis.RedisService;
import com.zyan.miaosha.service.MiaoshaUserService;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
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

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;

	@RequestMapping(value = "/to_list", produces = "text/html")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
		model.addAttribute("user", user);
		//取缓存
		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
//    	 return "goods_list";
		SpringWebContext ctx = new SpringWebContext(request,response,
				request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		//手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if(!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
	}

	@RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
	@ResponseBody
	public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
						  @PathVariable("goodsId")long goodsId) {
		model.addAttribute("user", user);

		//取缓存
		String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		//手动渲染
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";

		SpringWebContext ctx = new SpringWebContext(request,response,
				request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if(!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
		}
		return html;
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


	@RequestMapping(value="/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
										@PathVariable("goodsId")long goodsId) {
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setGoodsVo(goods);
		vo.setMiaoshaUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return Result.success(vo);
	}


	/**
	 * Get Post 区别
	 * get 幂等
	 * post
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
	@ResponseBody
	public Result<OrderInfo> list(Model model, MiaoshaUser user,
					   @RequestParam("goodsId")long goodsId) {
		model.addAttribute("user", user);
		if (user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		//判断库存
		GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goodsVo.getStockCount();
		if (stock <= 0){
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}
		//判断是否已经秒杀到
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsVo.getId());
		if (miaoshaOrder != null){
			model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
			return Result.error(CodeMsg.REPEAT_MIAOSHA);
		}
		//减库存，写订单，写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
		return Result.success(orderInfo);
	}
    
}
