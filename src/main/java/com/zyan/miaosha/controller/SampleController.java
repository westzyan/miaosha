package com.zyan.miaosha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-19 下午9:17
 */

@Controller
@RequestMapping("/demo1")
public class SampleController {

    @RequestMapping("/thymeleaf")
    @ResponseBody
    public String thymeleaf(Model model){
        model.addAttribute("name", "zhangyan");
        return "hello";
    }
}
