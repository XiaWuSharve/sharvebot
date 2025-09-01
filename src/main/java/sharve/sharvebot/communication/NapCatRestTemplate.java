package sharve.sharvebot.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class NapCatRestTemplate extends RestTemplate {
    private final static Logger logger = LoggerFactory.getLogger(NapCatRestTemplate.class);

    public NapCatRestTemplate(@Value("${bot.base-url}") String botUri) {
        super();
        setUriTemplateHandler(new DefaultUriBuilderFactory(botUri));
        getInterceptors().add((request, body, execution) -> {
            logger.info("Sending napCat body: {}", new String(body));
            ClientHttpResponse res = execution.execute(request, body);
            return res;
        });
    }
}
