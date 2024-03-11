package cxyjy.it.fbga.controller;

import lombok.var;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static cxyjy.it.fbga.controller.GPTController.hmacMd5;

public class SseClientExample {

    public static void main(String[] args) throws Exception {

        var timestamp = String.valueOf(System.currentTimeMillis());
        var appId = "burtdd";
        var path = "/chat/sse";
        String url = "https://gpt.kattgatt.com/chat/sse";
        String toEncrypt = path + "&" + timestamp + "&" + appId;

        OkHttpClient client = new OkHttpClient();

        var request = new Request.Builder().url("https://gpt.kattgatt.com/chat/sse")
                .addHeader("X-Request-AppId", appId)
                .addHeader("X-Request-Timestamp", timestamp)
                .addHeader("X-Request-Signature", hmacMd5(toEncrypt, "xxx"))
                .post(okhttp3.RequestBody.create("{\"msg\":\"红烧肉怎么做\"}", okhttp3.MediaType.get("application/json"))).build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.DAYS)
                .readTimeout(1, TimeUnit.DAYS)//
                .build();

        // 实例化EventSource，注册EventSource监听器
        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
            private long callStartNanos;

            private void printEvent(String name) {
                long nowNanos = System.nanoTime();
                if (name.equals("callStart")) {
                    callStartNanos = nowNanos;
                }
            }

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                printEvent("onOpen");
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                printEvent("onEvent");
                System.out.println(data);//请求到的数据
            }

            @Override
            public void onClosed(EventSource eventSource) {
                eventSource.cancel();
                printEvent("onClosed");
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                printEvent("onFailure");//这边可以监听并重新打开
            }
        });
        realEventSource.connect(okHttpClient);//真正开始请求的一步
    }

}
