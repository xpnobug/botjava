package com.tencent.botjava.base.config;

import static com.tencent.botjava.base.config.BaseFinal.APP_ID;
import static com.tencent.botjava.base.config.BaseFinal.APP_SECRET;
import static com.tencent.botjava.base.config.BaseFinal.BOT_TOKEN;
import static com.tencent.botjava.base.config.BaseFinal.CONNECTION_TIMEOUT;
import static com.tencent.botjava.base.config.BaseFinal.GATEWAY_BOT_URL;
import static com.tencent.botjava.base.config.BaseFinal.REQUEST_TIMEOUT;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.tencent.botjava.base.intents.Intents;
import com.tencent.botjava.base.intents.IntentsCalculator;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigInteger;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

/**
 * api事件
 *
 * @author 86136
 */
public class BotEvent {

    /**
     * 发送消息
     *
     * @param url
     * @param msgJson
     */
    public static void sendMsg(String url, JSONObject msgJson) {
        final HttpResponse res = HttpRequest.post(url)
            .connectionTimeout(CONNECTION_TIMEOUT)
            .timeout(REQUEST_TIMEOUT)
            .header("Content-Type", "application/json")
            .header("Authorization", BOT_TOKEN)
            .bodyText(msgJson.toString(), "application/json")
            .send();

        if (HttpServletResponse.SC_OK != res.statusCode()) {
            System.out.println("消息发送失败:" + res.bodyText());
        } else {
            System.out.println("消息发送成功:" + msgJson.toString());
        }
    }

    /**
     * 获取访问令牌
     *
     * @return 访问令牌字符串
     */
    public static String getAccessToken() {
        // 创建请求主体
        JSONObject requestBody = new JSONObject();
        requestBody.put("appId", APP_ID);
        requestBody.put("clientSecret", APP_SECRET);

        // 发送 POST 请求
        final HttpResponse res = HttpRequest.post("https://bots.qq.com/app/getAppAccessToken")
            .connectionTimeout(20000) // 连接超时：20秒
            .timeout(60000) // 请求超时：60秒
            .header("Content-Type", "application/json") // 设置请求头
            .body(requestBody.toString()) // 设置请求主体
            .send();

        // 检查响应状态码
        if (HttpServletResponse.SC_OK != res.statusCode()) {
            System.out.println("false");
        }
        // 设置响应的字符集
        res.charset("UTF-8");
        // 获取响应体文本
        String responseText = res.bodyText();
        JSONObject jsonObject = new JSONObject(responseText);
        return "QQBot " + jsonObject.getStr("access_token");
    }

    /**
     * 获取频道id 16093159052943820804
     * /users/@me/guilds
     *[
     * {"id":"16093159052943820804",
     * "name":"Create",
     * "icon":"https://groupprohead.gtimg.cn/620098353991000627/40?t=1700043037190",
     * "owner_id":"14115769874246966983",
     * "owner":false,"joined_at":"2023-11-12T23:49:17+08:00","member_count":8,"max_members":5000000,"description":""}]
     *
     * @return
     */
    public static String guilds() {
        final HttpResponse res = HttpRequest.get("https://api.sgroup.qq.com/users/@me/guilds")
            .connectionTimeout(CONNECTION_TIMEOUT)
            .timeout(REQUEST_TIMEOUT)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bot xxx.xxx")
            .send();

        // 检查响应状态码
        if (HttpServletResponse.SC_OK != res.statusCode()) {
            System.out.println("false");
        }
        // 设置响应的字符集
        res.charset("UTF-8");
        // 获取响应体文本
        return res.bodyText();
    }

    public static void main(String[] args) {
//        System.out.println(getAccessToken());
        System.out.println(guilds());
    }

    /**
     * 获取带分片 WSS 接入点
     */
    public static JSONObject fetchGatewayBotInfo() {
        String accessToken = getAccessToken();
        try {
            final HttpResponse res = HttpRequest.get(GATEWAY_BOT_URL)
                .connectionTimeout(CONNECTION_TIMEOUT)
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .send();

            if (HttpServletResponse.SC_OK != res.statusCode()) {
                System.out.println("请求失败，状态码：" + res.statusCode());
            } else {
                String responseBody = res.bodyText();
                JSONObject jsonObject = new JSONObject(responseBody);

                // 获取推荐的分片数
                int recommendedShards = jsonObject.getInt("shards");
                JSONObject sessionStartLimit = jsonObject.getJSONObject("session_start_limit");
                int maxConcurrency = sessionStartLimit.getInt("max_concurrency");

                // 计算分片ID todo
                BigInteger guildId = new BigInteger("16093159052943820804");
                int shardId = calculateShardId(guildId, recommendedShards);

                // 设置连接的属性
                JSONObject properties = new JSONObject();
                properties.put("$os", "linux");
                properties.put("$browser", "my_library");
                properties.put("$device", "my_library");

                // 构建连接的有效载荷
                JSONObject payload = new JSONObject();
                payload.put("op", 2);

                JSONObject data = new JSONObject();
                data.put("token", accessToken);
                data.put("intents", IntentsCalculator.calculateIntents(
                    // 订阅 GUILDS 相关事件
                    // Intents.GUILDS,

                    // 订阅 GUILD_MEMBERS 相关事件
                    // Intents.GUILD_MEMBERS,

                    // 订阅 GUILD_MESSAGES 相关事件，仅限私域机器人
                    // Intents.GUILD_MESSAGES,

                    // 订阅 GUILD_MESSAGE_REACTIONS 相关事件
                    // Intents.GUILD_MESSAGE_REACTIONS,

                    // 订阅 DIRECT_MESSAGE 相关事件，当收到用户发给机器人的私信消息时触发
//                    Intents.DIRECT_MESSAGE

                    // 订阅 OPEN_FORUMS_EVENT 相关事件
                    // Intents.OPEN_FORUMS_EVENT,

                    // 订阅 AUDIO_OR_LIVE_CHANNEL_MEMBER 相关事件，当用户进入或离开音视频/直播子频道时触发
//                    Intents.AUDIO_OR_LIVE_CHANNEL_MEMBER

                    // 订阅 INTERACTION 相关事件
                    // Intents.INTERACTION,

                    // 订阅 MESSAGE_AUDIT 相关事件
                    // Intents.MESSAGE_AUDIT,

                    // 订阅 FORUMS_EVENT 相关事件，仅限私域机器人
                    // Intents.FORUMS_EVENT,

                    // 订阅 AUDIO_ACTION 相关事件
                    // Intents.AUDIO_ACTION,

                    // 订阅 PUBLIC_GUILD_MESSAGES 相关事件，此为公域消息事件
                    Intents.PUBLIC_GUILD_MESSAGES
                ));

                data.put("shard", new JSONArray(new int[]{shardId, maxConcurrency}));
                data.put("properties", properties);

                payload.put("d", data);
                return payload;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请求或解析失败");
        }
        return null;
    }

    /**
     * 根据频道ID和推荐的分片数计算分片ID
     *
     * @param guildId   频道ID
     * @param numShards 推荐的分片数
     * @return 分片ID
     */
    private static int calculateShardId(BigInteger guildId, int numShards) {
        return guildId.shiftRight(22).mod(BigInteger.valueOf(numShards)).intValue();
    }
}
