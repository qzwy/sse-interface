package cxyjy.it.fbga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan("cxyjy.it.fbga")
public class FbgaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FbgaApplication.class, args);
	}

}
