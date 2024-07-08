package com.tencent.botjava.webscoket;

import com.tencent.botjava.service.impl.BotBaseFeatureMsg;
import com.tencent.botjava.service.impl.BotChatGptAiMsg;
import com.tencent.botjava.service.impl.BotChatGptMsg;
import com.tencent.botjava.service.impl.BotMenuMsg;

/**
 *
 */
public class BaseSendMsg {

    /**
     * * 发送消息的方法
     * @param content
     * @param msgId
     * @param sendMsgUrl
     * @param id
     */
    static void sendMsgsMethod(String content, String msgId, String sendMsgUrl, String id) {
        BotBaseFeatureMsg.feature(sendMsgUrl, content);
        // AI语言模型
        BotChatGptMsg.getChatGptMsg(content, msgId, sendMsgUrl, id);
        //菜单
        BotMenuMsg.getMenuMsg(content, msgId, sendMsgUrl, id);
        BotChatGptAiMsg.getChatGptMsg(content, msgId, sendMsgUrl, id);
    }
}
