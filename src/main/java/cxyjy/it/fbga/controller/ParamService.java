package cxyjy.it.fbga.controller;

import com.alibaba.fastjson.JSONObject;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamService {

  private final static Logger log = LoggerFactory.getLogger(ParamService.class);

  private static final String HMAC_MD5_ALGORITHM = "HmacMD5";
  private static final Charset UTF_8 = StandardCharsets.UTF_8;

  public static void main(String[] args) throws Exception {
    var timestamp = String.valueOf(System.currentTimeMillis());
    var appId = "burtdd";
    var path = "/chat/sse";
    String url = "https://gpt.kattgatt.com/chat/sse";
    String toEncrypt = path + "&" + timestamp + "&" + appId;
    String md5 = hmacMd5(toEncrypt, "A3b7H2p9");

    System.out.println("appId = " + appId);
    System.out.println("timestamp = " + timestamp);
    System.out.println("md5 = " + md5);
    System.out.println("url = " + url);
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

}
