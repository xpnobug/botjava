package com.tencent.botjava.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.tencent.botjava.base.config.BotEvent;
import com.tencent.botjava.service.BotBaseService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * AI语言模型
 *
 * @author 86136
 */
public class BotChatGptMsg extends BotBaseService {

    /**
     * 定义输出使用的功能列表
     */
    private static final Set<String> MSG_CONTENT = new HashSet<>(Arrays.asList(
        "<@!2745424269720774961> /功能"
    ));

    private static final Set<String> MSG_MH_CONTENT = new HashSet<>(Arrays.asList(
        "天气",
        "王者海报"
    ));

    /**
     * * 获取ChatGpt消息
     * @param content
     * @param msgId
     * @param sendMsgUrl
     * @param id
     */
    public static void getChatGptMsg(String content, String msgId, String sendMsgUrl, String id) {
        String msgMh = content.split(":")[0];
        if (!MSG_CONTENT.contains(content) && !MSG_MH_CONTENT.contains(msgMh)) {
            JSONObject msgContent = new JSONObject();
            String gptMsg =  getGpt3(content);
            msgContent.put("content", "<@!" + id + ">" + gptMsg);
            msgContent.put("msg_id", msgId);
            BotEvent.sendMsg(sendMsgUrl, msgContent);
        }
    }

    /**
     * * 发送Markdown格式消息
     * @param content
     * @param msgId
     * @param sendMsgUrl
     * @param id
     */
    public static void sendMarkdownMsg(String content, String msgId, String sendMsgUrl, String id) {
        String msgMh = content.split(":")[0];
        JSONObject msgContent = new JSONObject();
//        String gptMsg = BotEvent.getGpt3(content);

        // 创建 markdownContent 对象
        JSONObject markdownContent = new JSONObject();
//            markdownContent.put("custom_template_id", "102072731_1700207048");
        markdownContent.put("custom_template_id", "102072731_1719934567");

        // 创建 params 数组
        JSONArray params = new JSONArray();

        // 添加各个参数到 params 数组中 发送Markdown 格式
//            params.add(createParam("title", new String[]{"2024 频道年度个人报告"}));
//            params.add(createParam("data1", new String[]{"2024，我们拾级而上"}));
//            params.add(createParam("data2", new String[]{"时间记录下你的努力"}));
//            params.add(createParam("data3", new String[]{"也见证你的付出与担当"}));
//            params.add(createParam("data4", new String[]{"打开这封属于你的专属报告"}));
//            params.add(createParam("data5", new String[]{"绽放你的先进时刻"}));
//            params.add(createParam("title", new String[]{"段落2"}));
//            params.add(createParam("data1", new String[]{gptMsg}));

        params.add(createParam("title", new String[]{"2024 频道年度个人报告"}));
        params.add(createParam("author", new String[]{"11"}));
        params.add(createParam("time", new String[]{"时间记录下你的努力"}));
        params.add(createParam("content1", new String[]{"这里可以放代码块"}));
        params.add(createParam("content2", new String[]{"打开这封属于你的专属报告"}));

        // 将 params 数组放入 markdownContent
        markdownContent.put("params", params);

        // 将 markdownContent 对象放入 msgContent 对象中
        msgContent.put("markdown", markdownContent);
        // 发送消息
        BotEvent.sendMsg(sendMsgUrl, msgContent);
    }

    // 辅助方法，用于创建参数对象
    private static JSONObject createParam(String key, String[] values) {
        JSONObject param = new JSONObject();
        param.put("key", key);
        param.put("values", values);
        return param;
    }
}
