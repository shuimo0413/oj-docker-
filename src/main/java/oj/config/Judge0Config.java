package oj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Judge0配置类
 */
@Configuration
public class Judge0Config {

    // Judge0服务地址（本地部署）
    public static final String JUDGE0_BASE_URL = "http://localhost:2380";
    // Python 3的language_id（Judge0固定值）
    public static final Integer PYTHON3_LANGUAGE_ID = 71;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}