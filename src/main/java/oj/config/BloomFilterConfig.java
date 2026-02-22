package oj.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import oj.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@Configuration
public class BloomFilterConfig {

    @Autowired
    @Lazy
    private CommentMapper commentMapper;

    private BloomFilter<String> commentBloomFilter;

    @Bean
    public BloomFilter<String> commentBloomFilter() {
        this.commentBloomFilter = BloomFilter.create(
            Funnels.stringFunnel(Charset.defaultCharset()),
            10000,
            0.01
        );
        return this.commentBloomFilter;
    }

    public boolean mightContain(Integer commentId) {
        if (commentBloomFilter == null) {
            return true;
        }
        return commentBloomFilter.mightContain(String.valueOf(commentId));
    }

    public void put(Integer commentId) {
        if (commentBloomFilter != null) {
            commentBloomFilter.put(String.valueOf(commentId));
        }
    }

    public void initBloomFilter() {
        if (commentBloomFilter == null) {
            commentBloomFilter = commentBloomFilter();
        }

//        初始化的时候，查询所有的id扔进布隆过滤器
        List<Integer> allCommentIds = commentMapper.selectAllCommentIds();
        if (allCommentIds != null && !allCommentIds.isEmpty()) {
            for (Integer id : allCommentIds) {
                commentBloomFilter.put(String.valueOf(id));
            }
            log.info("布隆过滤器初始化完成，已加载 {} 条评论ID", allCommentIds.size());
        }
//        留一手，可能后面还需要布隆过滤器
    }
}
