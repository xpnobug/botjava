package com.tencent.botjava.webscoket;

import cn.hutool.json.JSONObject;
import com.tencent.botjava.base.config.BaseFinal;
import com.tencent.botjava.base.config.BotEvent;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

/**
 * 自定义 WebSocket 客户端类
 */
public class MyWebSocketClient extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketClient.class);

    /**
     * 使用 ScheduledExecutorService 代替 Timer，用于定时发送心跳
     */
    private ScheduledExecutorService heartbeatScheduler;
    private int reconnectAttempts = 0;

    /**
     * 保存会话信息和消息序列号
     */
    private String sessionId = null;
    private Long lastSequenceNumber = null;

    /**
     * 构造方法，传入 WebSocket 服务器的 URI
     *
     * @param serverUri WebSocket 服务器的 URI
     */
    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("WebSocket 已连接");
        // 检查 WebSocket 是否已连接
        if (!isOpen()) {
            logger.warn("WebSocket 连接已关闭");
            return;
        }
        // 每30秒发送一次心跳
        long heartbeatInterval = 30000;
        startHeartbeat(heartbeatInterval);

        // 获取并发送网关信息
        JSONObject getWayBotInfo = BotEvent.fetchGatewayBotInfo();
        if (!ObjectUtils.isEmpty(getWayBotInfo)) {
            send(getWayBotInfo.toString());
        }

        // 重置重连尝试次数
        reconnectAttempts = 0;
    }

    @Override
    public void onMessage(String message) {
        try {
            // 解析收到的消息
            JSONObject jsonMessage = new JSONObject(message);
            if (jsonMessage.containsKey("s")) {
                lastSequenceNumber = jsonMessage.getLong("s");
                String d = jsonMessage.get("d").toString();
                JSONObject jsonObject = new JSONObject(d);

                // 更新会话 ID
                if (jsonObject.containsKey("session_id") && !jsonObject.isNull("session_id")) {
                    sessionId = jsonObject.getStr("session_id");
                }
                // 处理消息内容
                if (jsonObject.containsKey("content") && !jsonObject.isNull("content")) {
                    String content = jsonObject.getStr("content");
                    String channelId = jsonObject.getStr("channel_id");
                    String msgId = jsonObject.getStr("id");
                    String user = jsonObject.getStr("author");
                    JSONObject userId = new JSONObject(user);
                    String id = userId.getStr("id");
                    String username = userId.getStr("username");
                    logger.info("收到消息: {} --- {}", username, content);

                    String sendMsgUrl = BaseFinal.getSendMsgUrl(channelId);
                    // 发送不同的消息
                    BaseSendMsg.sendMsgsMethod(content, msgId, sendMsgUrl, id);
                }
            }
        } catch (Exception e) {
            logger.error("解析消息时发生错误: {}", e.getMessage());
        }
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket 已关闭: {} - {}", code, reason);
        // 关闭心跳任务
        stopHeartbeat();

        // 获取错误对象
        WebSocketError error = getWebSocketError(code);
        if (error != null) {
            logger.warn("WebSocket 错误: {}", error);
            if (error.shouldReconnect() && reconnectAttempts < 10) {
                // 启动新线程进行重新连接
                Executors.newSingleThreadExecutor().execute(this::reconnect);
                logger.info("WebSocket 重新连接尝试中...");
                reconnectAttempts++;
            } else {
                logger.warn("超过最大重连尝试次数或不允许重连，不再重连");
            }
        }
    }

    /**
     * 启动心跳任务
     *
     * @param interval 心跳间隔时间（毫秒）
     */
    private void startHeartbeat(long interval) {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdown();
        }
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        heartbeatScheduler.scheduleAtFixedRate(this::sendHeartbeat, 0, interval,
            TimeUnit.MILLISECONDS);
    }

    /**
     * 停止心跳任务
     */
    private void stopHeartbeat() {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdown();
            heartbeatScheduler = null;
        }
    }

    // 发送心跳
    private void sendHeartbeat() {
        JSONObject payload = new JSONObject();
        // OpCode 1 用于心跳
        payload.put("op", 1);
        // 使用最新的消息序列号，如果是第一次连接，使用 null
        payload.put("d", lastSequenceNumber);
        send(payload.toString());
    }

    /**
     * 发送 Resume 消息，恢复连接
     */
    private void sendResume() {
        try {
            logger.info("尝试重新连接...");

            JSONObject payload = new JSONObject();
            // OpCode 6 用于 Resume 消息
            payload.put("op", 6);
            JSONObject data = new JSONObject();
            // 鉴权令牌
            data.put("token", BaseFinal.BOT_TOKEN);
            // 先前存储的会话 ID
            data.put("session_id", sessionId);
            // 最后一个消息的序列号
            data.put("seq", lastSequenceNumber);
            payload.put("d", data);
            send(payload.toString());

            logger.info("重新连接成功！");
        } catch (Exception e) {
            logger.error("重新连接发生错误: {}", e.getMessage());
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.error("发生错误: {}", ex.getMessage());
    }

    private WebSocketError getWebSocketError(int code) {
        switch (code) {
            case 4001:
                close();
                return WebSocketError.INVALID_OPCODE;
            case 4002:
                close();
                return WebSocketError.INVALID_PAYLOAD;
            case 4007:
                close();
                return WebSocketError.INVALID_SEQ;
            case 4006:
                close();
                return WebSocketError.INVALID_SESSION_ID;
            case 4008:
                close();
                return WebSocketError.PAYLOAD_TOO_FAST;
            case 4009:
                close();
                return WebSocketError.CONNECTION_EXPIRED;
            case 4010:
                close();
                return WebSocketError.INVALID_SHARD;
            case 4011:
                close();
                return WebSocketError.TOO_MANY_GUILDS;
            case 4012:
                close();
                return WebSocketError.INVALID_VERSION;
            case 4013:
                close();
                return WebSocketError.INVALID_INTENT;
            case 4014:
                close();
                return WebSocketError.INTENT_NO_PERMISSION;
            case 4914:
                close();
                return WebSocketError.BOT_UNLISTED;
            case 4915:
                close();
                return WebSocketError.BOT_BANNED;
            default:
                return WebSocketError.getInternalError(code);
        }
    }
}
