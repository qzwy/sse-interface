package cxyjy.it.fbga.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cxyjy.it.fbga.Utils.TokenUtils.generrateTocken;
import static cxyjy.it.fbga.controller.GPTController.hmacMd5;

@RestController
public class SSEController {

  final static Logger log = LoggerFactory.getLogger(SSEController.class);

  @GetMapping("/sse/aa1")
  public SseEmitter connect(@RequestParam String msg, @RequestParam(defaultValue = "") String chatId) throws Exception {

//    String message = "{\"msg\":\"" + msg + "\"}";
    String message = "{\"msg\": \"" + msg +"\"}";
    SSEServer sseServer = new SSEServer();
    SseEmitter connect = sseServer.connect("aa");
    HashSet<String> ids = new HashSet<>();
    ids.add("aa");

    var timestamp = String.valueOf(System.currentTimeMillis());
    var appId = "burtdd";
    var path = "/chat/sse";
    String url = "https://gpt.kattgatt.com/chat/sse";
    String toEncrypt = path + "&" + timestamp + "&" + appId;

//    OkHttpClient client = new OkHttpClient();
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)//
            .build();

    var request = new Request.Builder().url("https://gpt.kattgatt.com/chat/sse")
            .addHeader("X-Request-AppId", appId)
            .addHeader("X-Request-Timestamp", timestamp)
            .addHeader("X-Request-Signature", hmacMd5(toEncrypt, "A3b7H2p9"))
            .post(okhttp3.RequestBody.create(message, okhttp3.MediaType.get("application/json"))).build();

    // 实例化EventSource，注册EventSource监听器
    RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
      private long callStartNanos;

      private void printEvent(String name) {
        long nowNanos = System.nanoTime();
        if (name.equals("callStart")) {
          callStartNanos = nowNanos;
        }
//        System.out.print(name);
        log.info("Event: {}", name);
      }

      @Override
      public void onOpen(EventSource eventSource, Response response) {
        printEvent("onOpen");
      }

      @Override
      public void onEvent(EventSource eventSource, String id, String type, String data) {
        printEvent("onEvent");
        //发送
        sseServer.batchSendMessage(data, ids);
      }

      @Override
      public void onClosed(EventSource eventSource) {
        printEvent("onClosed");
        connect.complete();
      }

      @Override
      public void onFailure(EventSource eventSource, Throwable t, Response response) {
        printEvent("onFailure");//这边可以监听并重新打开
      }
    });

    realEventSource.connect(client);//真正开始请求的一步

    return connect;
  }



//  @GetMapping("/process")
//  public void sendMessage() throws InterruptedException {
//    for(int i=0; i<=100; i++) {
//      if (i > 50 && i < 70) {
//        Thread.sleep(500L);
//      } else {
//        Thread.sleep(100L);
//      }
//      log.info("{}", i);
//      SSEServer.batchSendMessage(String.valueOf(i));
//    }
////    SSEServer.closeSseEmitter();
//  }
}
