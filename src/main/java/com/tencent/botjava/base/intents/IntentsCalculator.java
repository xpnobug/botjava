package com.tencent.botjava.base.intents;

import java.util.Arrays;

/**
 * 计算需要订阅的事件的 intents 值
 */
public class IntentsCalculator {

    /**
     * 计算需要订阅的事件的 intents 值
     *
     * @param events 事件常量
     * @return 计算出的 intents 值
     */
    public static int calculateIntents(int... events) {
        return Arrays.stream(events).reduce(0, (a, b) -> a | b);
    }

    public static void main(String[] args) {
        // 示例：订阅 PUBLIC_GUILD_MESSAGES 和 GUILD_MEMBERS 事件
        int intents = IntentsCalculator.calculateIntents(
            Intents.GUILDS,
            Intents.GUILD_MEMBERS,
            Intents.GUILD_MESSAGES,
            Intents.GUILD_MESSAGE_REACTIONS,
            Intents.DIRECT_MESSAGE,
            Intents.OPEN_FORUMS_EVENT,
            Intents.AUDIO_OR_LIVE_CHANNEL_MEMBER,
            Intents.INTERACTION,
            Intents.MESSAGE_AUDIT,
            Intents.FORUMS_EVENT,
            Intents.AUDIO_ACTION,
            Intents.PUBLIC_GUILD_MESSAGES
        );
        System.out.println("需要订阅的 intents 值: " + intents);
    }
}
