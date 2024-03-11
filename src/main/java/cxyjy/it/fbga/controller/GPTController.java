package cxyjy.it.fbga.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import cxyjy.it.fbga.service.ApiService;
import java.util.ArrayList;
import java.util.List;
import lombok.var;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;


@RestController
public class GPTController {
    private final static Logger log = LoggerFactory.getLogger(GPTController.class);
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ApiService apiService;

    private static final String HMAC_MD5_ALGORITHM = "HmacMD5";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;


    @PostMapping("/gpt_test")
    public ResponseEntity<?> chatgpt(@RequestBody JSONObject msg) throws Exception {
//        msg = "{\"msg\":\"红烧肉怎么做\"}";
////        String result = "";
        String content = JSON.toJSONString(msg);
        System.out.println("content = " + content);

        var timestamp = String.valueOf(System.currentTimeMillis());
        var appId = "burtdd";
        var path = "/chat/sse";
        String url = "https://gpt.kattgatt.com/chat/sse";
        String toEncrypt = path + "&" + timestamp + "&" + appId;
        List<String> result = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        var requestr = new Request.Builder().url("https://gpt.kattgatt.com/chat/sse")
                .addHeader("X-Request-AppId", appId)
                .addHeader("X-Request-Timestamp", timestamp)
                .addHeader("X-Request-Signature", hmacMd5(toEncrypt, "xxx"))
                .post(okhttp3.RequestBody.create(content, okhttp3.MediaType.get("application/json"))).build();
        try (var response = client.newCall(requestr).execute()) {
            assert response.body() != null;
//            log.info("{}", response.body().string());
            result.add(response.body().string());

        }finally {

        }
        return ResponseEntity.ok(result);
    }


    @PostMapping("/gpt_test2")
    public ResponseEntity<?> chatgpt2(@RequestBody JSONObject msg) throws Exception {
//        msg = "{\"msg\":\"红烧肉怎么做\"}";
////        String result = "";
        String content = JSON.toJSONString(msg);
        System.out.println("content = " + content);

//        var timestamp = String.valueOf(System.currentTimeMillis());
//        var appId = "burtdd";
//        var path = "/chat/sse";
//        String url = "https://gpt.kattgatt.com/chat/sse";
//        String toEncrypt = path + "&" + timestamp + "&" + appId;
        List<String> result = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        var requestr = new Request.Builder().url("http://localhost:8020/sse/aa")
//                .addHeader("X-Request-AppId", appId)
//                .addHeader("X-Request-Timestamp", timestamp)
//                .addHeader("X-Request-Signature", hmacMd5(toEncrypt, "A3b7H2p9"))
                .post(okhttp3.RequestBody.create(content, okhttp3.MediaType.get("application/json"))).build();
        try (var response = client.newCall(requestr).execute()) {
            assert response.body() != null;
//            log.info("{}", response.body().string());
            result.add(response.body().string());

        }finally {

        }
        return ResponseEntity.ok(result);
    }



    public JSONObject GetParams() throws Exception {
        var timestamp = String.valueOf(System.currentTimeMillis());
        var appId = "burtdd";
        var path = "/chat/sse";
        String url = "https://gpt.kattgatt.com/chat/sse";
        String toEncrypt = path + "&" + timestamp + "&" + appId;
        String md5 = hmacMd5(toEncrypt, "A3b7H2p9");
        JSONObject result = new JSONObject();
        result.put("appId", appId);
        result.put("hmacMd5", md5);
        result.put("timestamp", timestamp);
        result.put("url", url);
        return result;
    }



    public static String hmacMd5(String data, String secret) throws Exception {
        log.info("data: {}", data);
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(UTF_8), HMAC_MD5_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_MD5_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmac = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hmac);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("No such algorithm: " + HMAC_MD5_ALGORITHM);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key: " + secret, e);
        }
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
