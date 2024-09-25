package com.kob.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pk/")
public class BotInformation {

    @RequestMapping("botinfor/")
    public Map<String,String> bot(){
        Map<String,String> map=new HashMap<>();
        map.put("name","eager");
        map.put("rating","1500");
        return map;
    }
}
