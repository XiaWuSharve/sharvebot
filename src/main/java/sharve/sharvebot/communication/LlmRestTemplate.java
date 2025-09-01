package sharve.sharvebot.communication;

import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageParam;
import com.openai.models.chat.completions.ChatCompletion.Choice;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Component
public class LlmRestTemplate {
    private final OpenAIClient client;
    private final ChatCompletionCreateParams.Builder paramsBuilder;
    private final static Logger logger = LoggerFactory.getLogger(LlmRestTemplate.class);

    public LlmRestTemplate(
            @Value("${openai.apiKey}") String apiKey,
            @Value("${openai.baseUrl}") String baseUrl) throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[] {};
                    }
                }
        };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        this.client = OpenAIOkHttpClient.builder()
                .fromEnv()
                .sslSocketFactory(sslContext.getSocketFactory())
                .trustManager((X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
        this.paramsBuilder = ChatCompletionCreateParams.builder()
                .model("deepseek-ai/DeepSeek-R1-Distill-Llama-8B");
    }

    public String create(List<ChatCompletionMessageParam> params) {
        ChatCompletionCreateParams createParams = this.paramsBuilder.messages(params).build();
        String requestString = createParams.messages().stream().map(m -> {
            if (m.isAssistant())
                return m.asAssistant().content().get().asText();
            else
                return m.asUser().content().asText();
        }).toList().toString();
        logger.info("Sending LLM request: {}", requestString);
        List<Choice> choices = client.chat().completions().create(createParams).choices();
        String res = choices.get(0).message().content().get();
        logger.info("Received LLM response: {}, request: {}", res, requestString);
        return res;
    }
}
