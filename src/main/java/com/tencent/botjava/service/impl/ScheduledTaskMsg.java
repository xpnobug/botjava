package com.tencent.botjava.service.impl;

/**
 * @author 86136
 */
public class ScheduledTaskMsg {

    public static void sendMsg(String content, String msgId, String sendMsgUrl) {
        if ("一键执行".equals(content)) {
            System.out.println("执行一键执行操作，触发新闻和天气任务。");

            // 调用BotNewsMsg的newsv5方法
            BotNewsMsg.startScheduledTask(sendMsgUrl);

            // 调用BotWeatherMsg的startScheduledTask方法
            BotWeatherMsg.startScheduledTask(content, msgId, sendMsgUrl);
        }
    }

}
