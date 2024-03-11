package cxyjy.it.fbga;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import cxyjy.it.fbga.controller.GPTController;
import lombok.var;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static cxyjy.it.fbga.controller.GPTController.hmacMd5;

@SpringBootTest
class FbgaApplicationTests {

	private final static Logger log = LoggerFactory.getLogger(GPTController.class);

	private static final String HMAC_MD5_ALGORITHM = "HmacMD5";
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

	@Autowired
	GPTController controller;

	@Test
	void contextLoads() throws Exception {

		String msg = "{\"msg\":\"红烧肉怎么做\"}";
		controller.chatgpt(JSONObject.parseObject(msg));
	}

}
