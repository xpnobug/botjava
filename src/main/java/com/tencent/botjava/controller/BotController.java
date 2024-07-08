package com.tencent.botjava.controller;


import com.tencent.botjava.webscoket.MyWebSocketClient;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot")
public class BotController {

    /**
     * 使用 ConcurrentHashMap 存储 WebSocketClient 实例
     */
    private final ConcurrentHashMap<String, MyWebSocketClient> webSocketClients = new ConcurrentHashMap<>();

    @GetMapping("/startBot")
    @ResponseBody
    public String startQQBot() {
        try {
            MyWebSocketClient client = new MyWebSocketClient(
                new URI("wss://api.sgroup.qq.com/websocket"));
            client.connect();
            // 将新的 WebSocketClient 放入 Map 中
            webSocketClients.put("qqBot", client);
//                sendEmail();
            return "WebSocket 连接已创建";
        } catch (Exception e) {
            e.printStackTrace();
            return "连接失败：" + e.getMessage();
        }
    }
}
