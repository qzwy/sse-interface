package cxyjy.it.fbga.controller;

import static cxyjy.it.fbga.controller.GPTController.hmacMd5;

import java.util.concurrent.TimeUnit;
import lombok.var;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

public class SseClientExample2 {

    public static void main(String[] args) throws Exception {

        OkHttpClient client = new OkHttpClient();

        var request = new Request.Builder().url("http://localhost:8021/sse/aa?msg=红烧肉怎么做").get().build();

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
                System.out.println("closed");
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
