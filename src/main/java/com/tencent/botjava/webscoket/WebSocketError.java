package com.tencent.botjava.webscoket;

/**
 * WebSocket 错误类，用于定义各种错误代码及其描述
 */
public class WebSocketError {

    private final int code;
    private final String description;
    private final boolean shouldReconnect;
    private final boolean shouldRetry;

    public WebSocketError(int code, String description, boolean shouldReconnect,
        boolean shouldRetry) {
        this.code = code;
        this.description = description;
        this.shouldReconnect = shouldReconnect;
        this.shouldRetry = shouldRetry;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean shouldReconnect() {
        return shouldReconnect;
    }

    public boolean shouldRetry() {
        return shouldRetry;
    }

    @Override
    public String toString() {
        return "WebSocketError{" +
            "code=" + code +
            ", description='" + description + '\'' +
            ", shouldReconnect=" + shouldReconnect +
            ", shouldRetry=" + shouldRetry +
            '}';
    }

    // 定义所有错误代码
    public static final WebSocketError INVALID_OPCODE = new WebSocketError(4001, "无效的 opcode",
        false, false);
    public static final WebSocketError INVALID_PAYLOAD = new WebSocketError(4002, "无效的 payload",
        false, false);
    public static final WebSocketError INVALID_SEQ = new WebSocketError(4007, "seq 错误", false,
        true);
    public static final WebSocketError INVALID_SESSION_ID = new WebSocketError(4006,
        "无效的 session id，无法继续 resume，请 identify", false, true);
    public static final WebSocketError PAYLOAD_TOO_FAST = new WebSocketError(4008,
        "发送 payload 过快，请重新连接，并遵守连接后返回的频控信息", true, true);
    public static final WebSocketError CONNECTION_EXPIRED = new WebSocketError(4009,
        "连接过期，请重连并执行 resume 进行重新连接", true, true);
    public static final WebSocketError INVALID_SHARD = new WebSocketError(4010, "无效的 shard",
        false, false);
    public static final WebSocketError TOO_MANY_GUILDS = new WebSocketError(4011,
        "连接需要处理的 guild 过多，请进行合理的分片", false, false);
    public static final WebSocketError INVALID_VERSION = new WebSocketError(4012, "无效的 version",
        false, false);
    public static final WebSocketError INVALID_INTENT = new WebSocketError(4013, "无效的 intent",
        false, false);
    public static final WebSocketError INTENT_NO_PERMISSION = new WebSocketError(4014,
        "intent 无权限", false, false);
    public static final WebSocketError INTERNAL_ERROR = new WebSocketError(4900, "内部错误，请重连",
        false, true);
    public static final WebSocketError BOT_UNLISTED = new WebSocketError(4914,
        "机器人已下架,只允许连接沙箱环境,请断开连接,检验当前连接环境", false, false);
    public static final WebSocketError BOT_BANNED = new WebSocketError(4915,
        "机器人已封禁,不允许连接,请断开连接,申请解封后再连接", false, false);

    // 错误代码范围4900~4913
    public static WebSocketError getInternalError(int code) {
        if (code >= 4900 && code <= 4913) {
            return new WebSocketError(code, "内部错误，请重连", false, true);
        }
        return null;
    }
}
