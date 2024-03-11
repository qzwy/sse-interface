package cxyjy.it.fbga.service;

import com.alibaba.fastjson.JSONObject;
import cxyjy.it.fbga.controller.GPTController;
import lombok.var;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static cxyjy.it.fbga.controller.GPTController.hmacMd5;

@Service
@Async
public class ApiService {

    private final static Logger log = LoggerFactory.getLogger(GPTController.class);

    @Autowired
    OkHttpClient okHttpClient;
    @Async
    public String sendRequest(String url) throws Exception {

        String content = "{\"msg\":\"红烧肉怎么做\"}";
//        controller.chatgpt(JSONObject.parseObject(msg));

        var timestamp = String.valueOf(System.currentTimeMillis());
        var appId = "smart-home";
        var path = "/chat/see";
//        String url = "https://gpt.kattgatt.com/chat/sse";
        String toEncrypt = path + "&" + timestamp + "&" + appId;


        Request request = new Request.Builder().url("https://gpt.kattgatt.com/chat/sse")
                .addHeader("X-Request-AppId", appId)
                .addHeader("X-Request-Timestamp", timestamp)
                .addHeader("X-Request-Signature", hmacMd5(toEncrypt, "BDxwmZUFKtbQd5x0"))
                .post(okhttp3.RequestBody.create(content, okhttp3.MediaType.get("application/json"))).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()){
                throw new IOException(String.valueOf(response));
            }
            assert response.body() != null;
            System.out.println("response = " + response.body().string());
            return Objects.requireNonNull(response.body()).string();
        }

    }
}
