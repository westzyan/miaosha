package com.zyan.miaosha.controller;

import com.zyan.miaosha.domain.User;
import com.zyan.miaosha.rabbitmq.MQSender;
import com.zyan.miaosha.redis.RedisService;
import com.zyan.miaosha.redis.UserKey;
import com.zyan.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zyan.miaosha.result.CodeMsg;
import com.zyan.miaosha.result.Result;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }
    //1.rest api json输出 2.页面
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello,imooc");
        // return new Result(0, "success", "hello,imooc");
    }


    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        mqSender.send("hello mq");
        return Result.success("hello,imooc");
        // return new Result(0, "success", "hello,imooc");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> mqTopic() {
        mqSender.sendTopic("hello mq");
        return Result.success("hello,imooc");
        // return new Result(0, "success", "hello,imooc");
    }

    //swagger
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> mqFanout() {
        mqSender.sendFanout("hello mq");
        return Result.success("hello,imooc");
        // return new Result(0, "success", "hello,imooc");
    }

    //swagger
    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> mqHeader() {
        mqSender.sendHeader("hello mq");
        return Result.success("hello,imooc");
        // return new Result(0, "success", "hello,imooc");
    }



    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
        //return new Result(500102, "XXX");
    }

    @RequestMapping("/thymeleaf")
    public String  thymeleaf(Model model) {
        model.addAttribute("name", "Joshua");
        return "hello";
    }

    @RequestMapping("/dbget")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/dbtx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        Boolean user = userService.tx();
        return Result.success(true);
    }
    @RequestMapping("/redisget")
    @ResponseBody
    public Result<User> redisGet(){
        User v1 = redisService.get(UserKey.getById,""+1 ,User.class);
        return Result.success(v1);
    }

    @RequestMapping("/redisset")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setName("555");
        user.setId(555);
        boolean v1 = redisService.set(UserKey.getById, "1",user);

        return Result.success(v1);
    }

}