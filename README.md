# BotJava

QQ频道机器人，折腾 JAVA SDK。

![Build](https://github.com/tencent-connect/botgo/actions/workflows/build.yml/badge.svg)
 

## 一、如何使用

### 1.请求 openapi 接口，操作资源

```java
// 获取并发送网关信息
JSONObject getWayBotInfo = BotEvent.fetchGatewayBotInfo();
```

### 2. 启动 websocket 连接，接收事件

[BotController.java](src%2Fmain%2Fjava%2Fcom%2Ftencent%2Fbotjava%2Fcontroller%2FBotController.java)
```java
 MyWebSocketClient client = new MyWebSocketClient(
    new URI("wss://api.sgroup.qq.com/websocket"));
            client.connect();
// 将新的 WebSocketClient 放入 Map 中
            webSocketClients.put("qqBot", client);
```    

[MyWebSocketClient.java](src%2Fmain%2Fjava%2Fcom%2Ftencent%2Fbotjava%2Fwebscoket%2FMyWebSocketClient.java)
```java
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
```

### 3.处理事件 [BotBaseService.java](src%2Fmain%2Fjava%2Fcom%2Ftencent%2Fbotjava%2Fservice%2FBotBaseService.java)

```java
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
 ```
## 二、生产环境中的一些建议

得益于 websocket 的机制，我们可以在本地就启动一个机器人，实现相关逻辑，但是在生产环境中需要考虑扩容，容灾等情况，所以建
议从以下几方面考虑生产环境的部署：

### 1.公域机器人，优先使用分布式 shard 管理

使用上面提到的分布式的 session manager 或者自己实现一个分布式的 session manager

### 2.提前规划好分片

分布式 SessionManager 需要解决的最大的问题，就是如何解决 shard 随时增加的问题，类似 kafka 的 rebalance 问题一样，
由于 shard 是基于频道 id 来进行 hash 的，所以在扩容的时候所有的数据都会被重新 hash。

提前规划好较多的分片，如 20 个分片，有助于在未来机器人接入的频道过多的时候，能够更加平滑的进行实例的扩容。比如如果使用的
是 `remote`，初始化时候分 20 个分片，但是只启动 2 个进程，那么这2个进程将争抢 20 个分片的消费权，进行消费，当启动更多
的实例之后，伴随着 websocket 要求一定时间进行一次重连，启动的新实例将会平滑的分担分片的数据处理。

### 3.接入和逻辑分离

接入是指从机器人平台收到事件的服务。逻辑是指处理相关事件的服务。

接入与逻辑分离，有助于提升机器人的事件处理效率和可靠性。一般实现方式类似于以下方案：

- 接入层：负责维护与平台的 websocket 连接，并接收相关事件，生产到 kafka 等消息中间件中。
  如果使用 `local` 那么可能还涉及到分布式锁的问题。可以使用sdk 中的 `sessions/remote/lock` 快速基于 redis 实现分布式锁。

- 逻辑层：从 kafka 消费到事件，并进行对应的处理，或者调用机器人的 openapi 进行相关数据的操作。

提前规划好 kafka 的分片，然后从容的针对逻辑层做水平扩容。或者使用 pulsar（腾讯云上叫 tdmq） 来替代 kafka 避免 rebalance 问题。

## 四、SDK 开发说明

请查看：[开发说明](./DEVELOP.md)

## 五、加入官方社区

欢迎扫码加入 **QQ 频道开发者社区**。

![开发者社区](https://mpqq.gtimg.cn/privacy/qq_guild_developer.png)