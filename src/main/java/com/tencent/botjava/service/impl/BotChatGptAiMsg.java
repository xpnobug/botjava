package com.tencent.botjava.service.impl;

import cn.hutool.json.JSONObject;

import com.tencent.botjava.base.config.BotEvent;
import com.tencent.botjava.service.BotBaseService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotChatGptAiMsg extends BotBaseService {

    /**
     * 获取@机器人的消息
     *
     * @param content    消息内容
     * @param msgId      消息id
     * @param sendMsgUrl 发送消息的url
     * @param id         机器人id
     */
    public static void getChatGptMsg(String content, String msgId, String sendMsgUrl, String id) {
        // 检查是否包含 '>' 并且 '>' 后面有内容
        if (content.contains(">") && content.split(">").length > 1) {
            String msgMh = content.split(">")[1].trim();
            JSONObject msgContent = new JSONObject();
            String aiMsg = getGpt3(msgMh);
            String url = extractUrl(aiMsg);
            if (url != null) {
                // 判断是否为@机器人
                if (content.contains("@")) {
//                    msgContent.put("content", "<@!" + id + ">" + aiMsg);
                    msgContent.put("msg_id", msgId);
                    msgContent.put("image", url);
                    BotEvent.sendMsg(sendMsgUrl, msgContent);
                }
            } else {
                // 判断是否为@机器人
                if (content.contains("@")) {
//                    msgContent.put("content", "<@!" + id + ">" + aiMsg);
                    msgContent.put("msg_id", msgId);
                    msgContent.put("image/file_image",
                        "https://alist.reaicc.com/nas/image/jpeg/2024-06/1/f3d22960-9cea-4720-b78b-408d3ef6764f.jpg");
                    BotEvent.sendMsg(sendMsgUrl, msgContent);
                }
            }

        } else {
            System.out.println("Message format is not correct or no message after '>'");
        }
    }

    public static String extractUrl(String content) {
        // 正则表达式模式，匹配各种常见的图像文件格式，并排除包含 "/download/" 的链接
        String urlPattern = "https?://(?![^\\s]*?/download/)[^\\s]+\\.(webp|jpg|jpeg|png|gif)";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

}
