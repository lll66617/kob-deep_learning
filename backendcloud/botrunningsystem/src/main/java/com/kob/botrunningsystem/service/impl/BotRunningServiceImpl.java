package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public final static BotPool botPool = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        //System.out.println("add bot: " + userId + " " + botCode + " " + input);
        //第一段地图#自己起始x坐标#自己起始y坐标#我的操作#对手起始x坐标#对手起始y坐标#对手操作
        System.out.println(input);
        botPool.addBot(userId, botCode, input);
        return "add bot success";
    }
}
