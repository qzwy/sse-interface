package cxyjy.it.fbga.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import cxyjy.it.fbga.controller.SSEController;
import lombok.var;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TokenUtils {
    final static Logger log = LoggerFactory.getLogger(TokenUtils.class);
    public static  String generrateTocken() {

        String apiKey = "e42278b7b01274f9d7fa9d278dcf01fa.Kc4MVwly7sEjTUQ6";
        String[] split = apiKey.split("\\.");
        String id = split[0];
        String secret = split[1];
        long ttlMillis = 3600000;

        Date timestamp = new Date();
        Date exp = new Date(timestamp.getTime() + ttlMillis);

        Map<String, Object> map  = new HashMap<>();
        map.put("alg","H256");
        map.put("sign_type","SIGN");

        //生成jwt加密token
        String token = JWT.create()
                .withHeader(map)//设置头信息
                .withClaim("api_key", id)//设置payload携带信息
                .withClaim("exp",exp)//设置payload携带信息
                .withClaim("timestamp", timestamp)//设置payload携带信息
                .withIssuedAt(timestamp)//设置时间戳
                .withExpiresAt(exp)//设置失效时间
                .sign(Algorithm.HMAC256(secret));//设置payload携带信息

        System.out.println(token);

        return token;
    }

    public static void main(String[] args) {
        String token = generrateTocken();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)//
                .build();

        String message = "{\"model\":\"glm-4\",\"messages\":[{\"role\":\"user\",\"content\":\"你好\"}],\"stream\":true}";
        var request = new Request.Builder().url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .addHeader("Authorization", "Bearer " + token)
                .post(okhttp3.RequestBody.create(message, okhttp3.MediaType.get("application/json"))).build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            log.info("{}", response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
