package com.tencent.botjava.service.impl;

import cn.hutool.json.JSONObject;
import com.tencent.botjava.base.config.BotEvent;
import com.tencent.botjava.service.BotBaseService;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 全球天气
 *
 * @author 86136
 */
public class BotWeatherMsg extends BotBaseService {

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean isFirstTime = true;

    public static void startScheduledTask(String content, String msgId, String sendMsgUrl) {

        // 如果content有数据，则不执行定时任务
        if (isFirstTime) {
            sendMessage(content, msgId, sendMsgUrl);
            System.out.println("第一次进入，执行初始化任务。");
            isFirstTime = false;
        } else if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("定时天气已启用，无需重复启动。");
            JSONObject newMsg = new JSONObject();
            newMsg.put("content", "定时天气已启用，无需重复启动。");
            BotEvent.sendMsg(sendMsgUrl, newMsg);
            return;
        }
        // 获取当前时间
        LocalTime now = LocalTime.now();

        // 计算距离下一天早上7点的时间差
        long initialDelay = calculateInitialDelay(now, LocalTime.of(7, 0));

        // 如果当前时间已经过了早上7点，将下一次任务推迟到明天
        if (now.isAfter(LocalTime.of(7, 0))) {
            initialDelay += TimeUnit.DAYS.toMinutes(1);
        }

        // 启动定时任务，每天早上7点执行一次
        scheduler.scheduleAtFixedRate(() -> sendScheduledMessage(content, msgId, sendMsgUrl),
            initialDelay,
            TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
    }

    private static long calculateInitialDelay(LocalTime currentTime, LocalTime scheduledTime) {
        return Duration.between(currentTime, scheduledTime).toMinutes();
    }

    private static void sendScheduledMessage(String content, String msgId, String sendMsgUrl) {
        JSONObject msgContent = new JSONObject();
        String weather = getWeather("北京", "1");
        msgContent.put("content", weather);
        msgContent.put("msg_id", msgId);
        BotEvent.sendMsg(sendMsgUrl, msgContent);

    }

    private static void sendMessage(String content, String msgId, String sendMsgUrl) {
        JSONObject msgContent = new JSONObject();
        String tq = content.split(":")[0];
        if ("天气".equals(tq)) {
            String weatherName = content.split(":")[1];
            String weather = getWeather(weatherName, "1");
            msgContent.put("content", weather);
            msgContent.put("msg_id", msgId);
            BotEvent.sendMsg(sendMsgUrl, msgContent);
        }
    }

}
