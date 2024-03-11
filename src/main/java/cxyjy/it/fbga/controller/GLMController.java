package cxyjy.it.fbga.controller;


import cxyjy.it.fbga.service.SSEServer;
import lombok.var;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cxyjy.it.fbga.Utils.TokenUtils.generrateTocken;

/**
 * 调用GLM-Api
 * sse
 */
@RestController
public class GLMController {
    private final static Logger log = LoggerFactory.getLogger(GLMController.class);
    @GetMapping("/sse/aa")
    public SseEmitter connect2(@RequestParam String msg, @RequestParam(defaultValue = "") String chatId) {
        //构造请求结构
        String message = "{\"model\":\"glm-4\",\"messages\":[{\"role\":\"user\",\"content\":\"" + msg + "\"}],\"stream\":true}";
        //创建sse服务
        SSEServer sseServer = new SSEServer();
        SseEmitter connect = sseServer.connect("aa");
        HashSet<String> ids = new HashSet<>();
        ids.add("aa");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)//
                .build();
        //生成jwt的token
        String token = generrateTocken();
        var request = new Request.Builder().url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .addHeader("Authorization", "Bearer " + token)
                .post(okhttp3.RequestBody.create(message, okhttp3.MediaType.get("application/json"))).build();

        // 实例化EventSource，注册EventSource监听器
        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
            private long callStartNanos;
            private void printEvent(String name) {
                long nowNanos = System.nanoTime();
                if (name.equals("callStart")) {
                    callStartNanos = nowNanos;
                }
                log.info("Event: {}", name);
            }
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                printEvent("onOpen");
            }
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                //正则表达式查找content里的内容
                String regex = "\"content\":\"([^\"]+)\"";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(data);
                if (matcher.find()) {
                    // 获取匹配到的content的值
                    String content = matcher.group(1);
                    //发送
                    log.info("Event : {}, message: {}", "onEvent", content);
                    String send = "{\"msg\":\"" +content + "\",\"chatId\":\" \"}";
                    sseServer.batchSendMessage(send, ids);
                } else {
                    System.out.println("Content not found.");
                }
            }
            @Override
            public void onClosed(EventSource eventSource) {
                printEvent("onClosed");
                connect.complete();
            }
            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                printEvent("onFailure");//这边可以监听并重新打开
                connect.complete();
            }
        });
        realEventSource.connect(client);//真正开始请求的一步
        return connect;
    }
}
