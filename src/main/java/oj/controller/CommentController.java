package oj.controller;


import lombok.extern.slf4j.Slf4j;
import oj.config.BloomFilterConfig;
import oj.constant.RedisCacheKeyConstants;
import oj.pojo.dto.CancelCommentLikeDTO;
import oj.pojo.dto.CommentLikeDTO;
import oj.pojo.entity.Result;
import oj.pojo.dto.CommentDTO;
import oj.pojo.vo.CommentVO;
import oj.service.CommentService;
import oj.util.RedisLockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BloomFilterConfig bloomFilterConfig;
    @Autowired
    private RedisLockUtils redisLockUtils;


    @PostMapping("/addComment")
    public Result addComment(@RequestBody CommentDTO commentDTO) throws IllegalAccessException {
        log.info("新增评论：{}", commentDTO);
        Integer commentId = commentService.addComment(commentDTO);
        if (commentId != null) {
            bloomFilterConfig.put(commentId);
        }
        return Result.success();
    }

    @PostMapping("/addCommentLike")
    public Result addCommentLikeDTO(@RequestBody CommentLikeDTO commentLikeDTO) throws IllegalAccessException {
        log.info("新增评论点赞：{}", commentLikeDTO);
        commentService.addCommentLikeDTO(commentLikeDTO);
        String cacheKey = RedisCacheKeyConstants.COMMENT_DETAIL_KEY_PREFIX + commentLikeDTO.getCommentId();
        redisTemplate.delete(cacheKey);
        return Result.success();
    }
//    获取单条评论
    @GetMapping("/getComment")
    public Result getComment(@RequestParam Integer commentId) {
        CommentVO commentVO = commentService.getCommentById(commentId);
        return Result.success(commentVO);
    }

//    获取一道题的所有评论（分页）
    @GetMapping("/getComments")
    public Result getComments(@RequestParam Integer questionId,
                              @RequestParam Integer pageNum,
                              @RequestParam Integer pageSize) {


        List<Integer> commentIds = commentService.selectCommentIds(questionId, pageNum, pageSize);
        List<CommentVO> commentVOList = new ArrayList<>();


        for (Integer commentId : commentIds) {
            if (!bloomFilterConfig.mightContain(commentId)) {
                log.info("布隆过滤器判断评论{}不存在，跳过", commentId);
                continue;
            }

            String cacheKey = RedisCacheKeyConstants.COMMENT_DETAIL_KEY_PREFIX + commentId;
            String lockKey = "comment:" + commentId;
            
            CommentVO commentVO = (CommentVO) redisTemplate.opsForValue().get(cacheKey);
            if (commentVO != null) {
                log.info("缓存命中：评论{}", commentId);
                commentVOList.add(commentVO);
                continue;
            }

            boolean locked = false;
            try {
//                试试获取锁
                locked = redisLockUtils.tryLock(lockKey, 1, 10, TimeUnit.SECONDS);
                if (locked) {
                    commentVO = (CommentVO) redisTemplate.opsForValue().get(cacheKey);
                    if (commentVO != null) {
                        log.info("获取锁后缓存命中：评论{}", commentId);
                        commentVOList.add(commentVO);
                        continue;
                    }

                    commentVO = commentService.getCommentById(commentId);
                    if (commentVO != null) {
//                        防止雪崩，使用随机过期时间
                        long expireMinutes = RedisCacheKeyConstants.getRandomExpireMinutes(
                            RedisCacheKeyConstants.COMMENT_DETAIL_EXPIRE_MINUTES,
                            RedisCacheKeyConstants.COMMENT_DETAIL_EXPIRE_RANDOM_MINUTES);

                        redisTemplate.opsForValue().set(cacheKey, commentVO, expireMinutes, TimeUnit.MINUTES);
                        commentVOList.add(commentVO);
                    }
                } else {
                    log.info("获取锁失败，尝试直接查询数据库：评论{}", commentId);
                    commentVO = commentService.getCommentById(commentId);
                    if (commentVO != null) {
                        commentVOList.add(commentVO);
                    }
                }
            } finally {
                if (locked) {
                    redisLockUtils.unlock(lockKey);
                }
            }
        }
        return Result.success(commentVOList);
    }

//    删除评论
    @DeleteMapping("/deleteComment")
    public Result removeComment(@RequestParam Integer id){
        log.info("删除评论：{}", id);
        List<Integer> deletedCommentIds = commentService.deleteCommentById(id);
        for (Integer commentId : deletedCommentIds) {
            String cacheKey = RedisCacheKeyConstants.COMMENT_DETAIL_KEY_PREFIX + commentId;
            redisTemplate.delete(cacheKey);
        }
        return Result.success();
    }


//    取消点赞
    @DeleteMapping("/cancelCommentLike")
    public Result cancelCommentLike(@RequestBody CancelCommentLikeDTO cancelCommentLikeDTO){
        commentService.cancelCommentLike(cancelCommentLikeDTO);
        String cacheKey = RedisCacheKeyConstants.COMMENT_DETAIL_KEY_PREFIX + cancelCommentLikeDTO.getCommentId();
        redisTemplate.delete(cacheKey);
        return Result.success();
    }
}
