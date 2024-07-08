package com.tencent.botjava.base.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置类，用于存储QQ机器人基础配置
 * <p>
 * 统一地址：https://api.sgroup.qq.com 获取带分片 WSS 接入点API 发送消息API
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "qqbot.base")
public class BaseFinal {

    /**
     * AppID (机器人ID)
     */
    private String appId;
    public static String APP_ID;

    /**
     * AppSecret(机器人密钥)
     */
    private String appSecret;
    public static String APP_SECRET;

    /**
     * token
     */
    private String botToken;

    /**
     * 统一地址 https://api.sgroup.qq.com
     */
    private String baseUrl;

    /**
     * 获取带分片 WSS 接入点API
     */
    private String gatewayBotUrl;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 发送消息的URL模板
     */
    public static String SEND_MSG;
    public static String BOT_TOKEN;
    public static String GATEWAY_BOT_URL;

    /**
     * 连接超时时间（毫秒）
     */
    public static final int CONNECTION_TIMEOUT = 20000;

    /**
     * 请求超时时间（毫秒）
     */
    public static final int REQUEST_TIMEOUT = 60000;

    /**
     * 设置baseUrl并更新发送消息的URL模板
     *
     * @param baseUrl 基础URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        BaseFinal.SEND_MSG = baseUrl + "/channels/%s/messages";
    }

    /**
     * 设置机器人token并更新静态变量
     *
     * @param token 机器人token
     */
    @Value("${qqbot.base.bot-token}")
    public void setBotToken(String token) {
        this.botToken = token;
        BaseFinal.BOT_TOKEN = token;
    }

    /**
     * 设置gatewayBotUrl并更新静态变量
     *
     * @param gatewayBotUrl 获取带分片 WSS 接入点API的URL
     */
    @Value("${qqbot.base.gateway-bot-url}")
    public void setGatewayBotUrl(String gatewayBotUrl) {
        this.gatewayBotUrl = gatewayBotUrl;
        BaseFinal.GATEWAY_BOT_URL = gatewayBotUrl;
    }

    public void setAppId(String appId) {
        this.appId = appId;
        BaseFinal.APP_ID = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
        BaseFinal.APP_SECRET = appSecret;
    }

    /**
     * 初始化方法，在Bean创建后调用
     */
    @PostConstruct
    public void init() {
        if (isEnabled()) {
            startBot();
        } else {
            stopBot();
        }
    }

    /**
     * 销毁方法，在Bean销毁前调用
     */
    @PreDestroy
    public void destroy() {
        stopBot();
    }

    /**
     * 启动机器人
     */
    public void startBot() {
        // 实现启动机器人的逻辑
        System.out.println("QQ机器人已启动");
        // 这里可以放置启动机器人所需的实际逻辑
    }

    /**
     * 关闭机器人
     */
    public void stopBot() {
        // 实现关闭机器人的逻辑
        System.out.println("QQ机器人已关闭");
        // 这里可以放置关闭机器人所需的实际逻辑
    }

    /**
     * 检查配置是否启用
     *
     * @return 配置是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 根据频道ID获取发送消息的URL
     *
     * @param channelId 频道ID
     * @return 发送消息的URL
     */
    public static String getSendMsgUrl(String channelId) {
        return String.format(SEND_MSG, channelId);
    }
}
