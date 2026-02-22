package oj.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
//自动启动这一块
@Component
public class BloomFilterInitializer implements CommandLineRunner {

    @Autowired
    private BloomFilterConfig bloomFilterConfig;

    @Override
    public void run(String... args) {
        log.info("开始初始化布隆过滤器...");
        bloomFilterConfig.initBloomFilter();
    }
}
