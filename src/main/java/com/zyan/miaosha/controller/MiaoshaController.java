package com.zyan.miaosha.controller;

import com.zyan.miaosha.access.AccessLimit;
import com.zyan.miaosha.rabbitmq.MQSender;
import com.zyan.miaosha.rabbitmq.MiaoshaMessage;
import com.zyan.miaosha.redis.AccessKey;
import com.zyan.miaosha.redis.GoodsKey;
import com.zyan.miaosha.redis.MiaoshaKey;
import com.zyan.miaosha.util.MD5Util;
import com.zyan.miaosha.util.UUIDUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;

	@Autowired
	MQSender mqSender;

	private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();


	/**
	 * 系统初始化
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
		if (goodsVoList == null){
			return;
		}
		for (GoodsVo goodsVo : goodsVoList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goodsVo.getId(), goodsVo.getStockCount());
			localOverMap.put(goodsVo.getId(), false);
		}
	}
	
	/**
	 * QPS:1306
	 * 5000 * 10
	 * */
	/**
	 *  GET POST有什么区别？
	 * */
    @RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
								   @RequestParam("goodsId")long goodsId,
								   @PathVariable("path") String path) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	//验证path
		boolean check = miaoshaService.checkPath(user,goodsId,path);
    	if (!check){
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}


    	/**
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		return Result.error(CodeMsg.MIAOSHA_OVER);
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEAT_MIAOSHA);
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);
		 */

    	//内存标记，减少redis访问
		Boolean over = localOverMap.get(goodsId);
		if (over){
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}

		//预减库存
    	long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
    	if (stock < 0){
    		localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}

		//判断是否已经秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}

		//入队
		MiaoshaMessage mm = new MiaoshaMessage();
		mm.setUser(user);
		mm.setGoodsId(goodsId);
		mqSender.sendMiaoshaMessage(mm);

		return Result.success(0);//排队中

    }


	/**
	 * orderid:成功
	 * -1 秒杀失败
	 * 0：排队中
	 * @param user
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value="/result", method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(MiaoshaUser user,
								   @RequestParam("goodsId")long goodsId) {
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
		return Result.success(result);
	}


	@AccessLimit(seconds=5, maxCount=5, needLogin=true)
	@RequestMapping(value="/path", method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
										 @RequestParam("goodsId")long goodsId,
										 @RequestParam(value="verifyCode", defaultValue="0")int verifyCode
	) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
		if(!check) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		String path  =miaoshaService.createMiaoshaPath(user, goodsId);
		return Result.success(path);
	}

	@RequestMapping(value="/verifyCody", method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser user,
											   @RequestParam("goodsId")long goodsId) {
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

		BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
		try {
			OutputStream out = response.getOutputStream();
			ImageIO.write(image, "JPEG", out);
			out.flush();
			out.close();
		}catch (Exception e){
			e.printStackTrace();
			return Result.error(CodeMsg.MIAOSHA_FAIL);
		}
		return null;

	}

}
