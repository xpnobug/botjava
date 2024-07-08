package com.tencent.botjava.base.intents;

/**
 * 定义事件的位移常量
 */
public class Intents {

    public static final int GUILDS = 1 << 0; // guild相关事件
    public static final int GUILD_MEMBERS = 1 << 1; // guild成员相关事件
    public static final int GUILD_MESSAGES = 1 << 9; // guild消息事件，仅限私域机器人
    public static final int GUILD_MESSAGE_REACTIONS = 1 << 10; // guild消息反应事件
    public static final int DIRECT_MESSAGE = 1 << 12; // 私信消息事件
    public static final int OPEN_FORUMS_EVENT = 1 << 18; // 公域论坛事件
    public static final int AUDIO_OR_LIVE_CHANNEL_MEMBER = 1 << 19; // 音视频/直播子频道成员进出事件
    public static final int INTERACTION = 1 << 26; // 互动事件
    public static final int MESSAGE_AUDIT = 1 << 27; // 消息审核事件
    public static final int FORUMS_EVENT = 1 << 28; // 私域论坛事件
    public static final int AUDIO_ACTION = 1 << 29; // 音频动作事件
    public static final int PUBLIC_GUILD_MESSAGES = 1 << 30; // 公域消息事件
}
