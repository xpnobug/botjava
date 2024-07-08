package com.tencent.botjava.service.impl;

import cn.hutool.json.JSONObject;
import com.tencent.botjava.base.config.BotEvent;
import com.tencent.botjava.service.BotBaseService;

/**
 * @author 86136
 */
public class MessageImpl extends BotBaseService {

    public static void getMusic(String content, String msgId, String sendMsgUrl, String id) {
        if ("music".equals(content)) {
            JSONObject msgContent = new JSONObject();
            String wyy = getcyApi();
            msgContent.put("content", "<@!" + id + ">" + wyy);
            msgContent.put("msg_id", msgId);
            BotEvent.sendMsg(sendMsgUrl, msgContent);
        }
    }

    public static void getRandomPic(String sendMsgUrl, String content) {
        if ("图片".equals(content)) {
            String random4kPic = getRandom4kPic();
            JSONObject imgMsg = new JSONObject();
            imgMsg.put("image", random4kPic);
            BotEvent.sendMsg(sendMsgUrl, imgMsg);
        }
    }

}
