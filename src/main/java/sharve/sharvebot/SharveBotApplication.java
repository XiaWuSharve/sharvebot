package sharve.sharvebot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SharveBotApplication implements CommandLineRunner {
	@Value("${server.port}")
	private String sharveBotPort;

	private static final Logger logger = LoggerFactory.getLogger(SharveBotApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SharveBotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Sharvebot is starting with port: {}", sharveBotPort);
	}
}
