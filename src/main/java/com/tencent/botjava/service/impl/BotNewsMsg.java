package com.tencent.botjava.service.impl;

import cn.hutool.json.JSONObject;
import com.tencent.botjava.base.config.BotEvent;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 新闻消息
 *
 * @author 86136
 */
public class BotNewsMsg {

    private static ScheduledExecutorService scheduler;

    public static void newsv5(String sendMsgUrl, String content) {
        if ("启用定时新闻".equals(content)) {
            startScheduledTask(sendMsgUrl);
        } else if ("关闭定时新闻".equals(content)) {
            stopScheduledTask(sendMsgUrl);
        }
    }

    public static void startScheduledTask(String sendMsgUrl) {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("定时新闻已启用，无需重复启动。");
            JSONObject newMsg = new JSONObject();
            newMsg.put("content", "定时新闻已启用，无需重复启动。");
            BotEvent.sendMsg(sendMsgUrl, newMsg);
            return;
        }

        // 获取当前时间和早上9点时间
        LocalTime now = LocalTime.now();
        LocalTime scheduledTime = LocalTime.of(9, 0);

        // 计算直到下一次早上9点的初始延迟
        long initialDelay = calculateInitialDelay(now, scheduledTime);

        // 如果当前时间已经过了早上9点，将下一次任务推迟到明天
        if (now.isAfter(scheduledTime)) {
            initialDelay += TimeUnit.DAYS.toMinutes(1);
        }

        // 创建一个新的定时线程池
        scheduler = Executors.newScheduledThreadPool(1);

        // 启动定时任务，每天早上9点执行一次
        scheduler.scheduleAtFixedRate(() -> sendScheduledMessage(sendMsgUrl), initialDelay,
            TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
    }

    private static long calculateInitialDelay(LocalTime currentTime, LocalTime scheduledTime) {
        return Duration.between(currentTime, scheduledTime).toMinutes();
    }

    private static void stopScheduledTask(String sendMsgUrl) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("定时新闻已关闭。");
            JSONObject newMsg = new JSONObject();
            newMsg.put("content", "定时新闻已关闭。");
            BotEvent.sendMsg(sendMsgUrl, newMsg);
        } else {
            System.out.println("定时新闻未启用，无需关闭。");
            JSONObject newMsg = new JSONObject();
            newMsg.put("content", "定时新闻未启用，无需关闭。");
            BotEvent.sendMsg(sendMsgUrl, newMsg);
        }
    }

    private static void sendScheduledMessage(String sendMsgUrl) {
        JSONObject newMsg = new JSONObject();
        newMsg.put("content", "定时新闻已启用,需要关闭请发送：关闭定时新闻");
        newMsg.put("image", "https://zj.v.api.aa1.cn/api/60s-v2/?cc=%E5%B0%8F%E4%B8%83");
        BotEvent.sendMsg(sendMsgUrl, newMsg);
    }
}
