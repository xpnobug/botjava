package com.tencent.botjava.service.impl;

import cn.hutool.json.JSONObject;
import com.tencent.botjava.base.config.BotEvent;
import com.tencent.botjava.service.BotBaseService;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * AI语言模型
 *
 * @author 86136
 */
public class BotMenuMsg extends BotBaseService {

    /**
     * 定义输出使用的功能列表
     */
    private static final Set<String> MSG_CONTENT = new HashSet<>(Arrays.asList(
        "/功能"
    ));

    private static final Set<String> MSG_MH_CONTENT = new HashSet<>(Arrays.asList(
        "天气",
        "王者海报"
    ));

    /**
     * 获取菜单信息
     *
     * @param content
     * @param msgId
     * @param sendMsgUrl
     * @param id
     */
    public static void getMenuMsg(String content, String msgId, String sendMsgUrl, String id) {
        if (content.contains("功能")) {
            JSONObject msgContent = new JSONObject();
            msgContent.put("content", "<@!" + id + ">" +
                "\n 功能列表\n 1.天气查询  \n 2.飞鸟集语录 \n 3.签到打卡 \n 4.等待添加");
            msgContent.put("msg_id", msgId);
            BotEvent.sendMsg(sendMsgUrl, msgContent);
        }
        if (content.contains("天气")) {
            getWeather(content, msgId, sendMsgUrl, id);
        }
        if (content.contains("玩法指南")) {
            JSONObject msgContent = new JSONObject();
            msgContent.put("content", "<@!" + id + ">" +
                "发送【功能】即可 查看所有功能 ！");
            msgContent.put("msg_id", msgId);
            BotEvent.sendMsg(sendMsgUrl, msgContent);
        }
        if (content.contains("签到打卡")) {
            getQdao(content, msgId, sendMsgUrl, id);
        }
    }

    /**
     * 获取天气信息
     *
     * @param content
     * @param msgId
     * @param sendMsgUrl
     * @param id
     */
    private static void getWeather(String content, String msgId, String sendMsgUrl, String id) {
        JSONObject msgContent = new JSONObject();
        // 正则表达式匹配中文和英文的“天气”及中英文冒号
        String regex = "天气[:：]|weather[:：]";
        String[] parts = content.split(regex);

        if (parts.length > 1) {
            String weatherName = parts[1].trim(); // 去除前后空格
            String weather = getWeather(weatherName, "1");
            msgContent.put("content", weather);
        } else {
            msgContent.put("content", "<@!" + id + ">" + "请指定要查询的地区，例如： 天气：北京");
        }
        msgContent.put("msg_id", msgId);
        BotEvent.sendMsg(sendMsgUrl, msgContent);
    }

    private static void getQdao(String content, String msgId, String sendMsgUrl, String id) {
        JSONObject msgContent = new JSONObject();
        msgContent.put("content",
            "<@!" + id + ">" + "签到成功！今天是" + new Date() + "明天继续加油！");
        msgContent.put("msg_id", msgId);
        BotEvent.sendMsg(sendMsgUrl, msgContent);
    }

}
