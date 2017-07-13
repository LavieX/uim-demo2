package com.ca.atlas.sample;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.launchdarkly.client.*;

@SpringBootApplication
@RestController


public class Application {

	private static Logger LOGGER = Logger.getLogger(Application.class.getName());

    static LDConfig config = new LDConfig.Builder()
        .connectTimeout(3)
        .socketTimeout(3)
        .build();
    static LDClient ldClient = new LDClient("sdk-196cbe83-740b-4712-aae8-ee07f14a88bc");

    static LDUser user = new LDUser.Builder("aa0ceb")
        .anonymous(true)
        .build();

	@RequestMapping("/")
	public String home() {
		LOGGER.info("Serving root");
    boolean showFeature = ldClient.boolVariation("test-flag", user, false);
    if (showFeature) {
        // application code to show the feature
        return "Feature enabled!";
    }
    else {
        return "Hello, Big World!";

        // the code to run if the feature is off
    }
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
