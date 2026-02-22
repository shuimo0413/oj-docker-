package oj.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import oj.pojo.dto.CancelCommentLikeDTO;
import oj.pojo.dto.CommentDTO;
import oj.pojo.dto.CommentLikeDTO;
import oj.pojo.entity.CommentLike;
import oj.pojo.vo.CommentVO;
import oj.mapper.CommentLikeMapper;
import oj.mapper.CommentMapper;
import oj.pojo.entity.Comment;
import oj.mapper.UserMapper;
import oj.service.CommentService;
import oj.util.ClassMergeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;
    @Autowired
    private UserMapper userMapper;


    @Override
    public Integer addComment(CommentDTO commentDTO){
        Comment comment = ClassMergeUtils.merge(commentDTO, new Comment());
        log.info("评论：{}", comment);
        commentMapper.insert(comment);
        return comment.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addCommentLikeDTO(CommentLikeDTO commentLikeDTO) {
        Integer userId = commentLikeDTO.getUserId();
        Integer commentId = commentLikeDTO.getCommentId();
        
        int exists = commentLikeMapper.countByUserIdAndCommentId(userId, commentId);
        if (exists > 0) {
            log.info("用户{}已点赞评论{}，跳过", userId, commentId);
            return;
        }
        
        CommentLike commentLike = ClassMergeUtils.merge(commentLikeDTO, new CommentLike());
        commentLikeMapper.insert(commentLike);
        commentMapper.addLikeCount(commentId, 1);
    }

    @Override
    public CommentVO getCommentById(Integer commentId) {
        CommentVO commentVO = new CommentVO();

//        先获取本评论的内容
        Comment comment = commentMapper.selectById(commentId);
        commentVO = ClassMergeUtils.merge(comment, commentVO);
        // 根据userId获取用户名
        commentVO.setUserName(userMapper.selectById(comment.getUserId()).getUsername());

//        获取子评论的内容
        ArrayList<Integer> commentIds = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(commentId);
        while (!queue.isEmpty()) {
//            获取当前队列的头元素
            Integer cid = queue.poll();
            commentIds.add(cid);

            ArrayList<Integer> tempCommentIds = commentMapper.selectChildCommentId(cid);
            if (tempCommentIds != null && !tempCommentIds.isEmpty()){
                for (Integer id : tempCommentIds) {
                    queue.offer(id);
                }
            }
        }
        // 移除根评论ID，避免子评论列表包含自身
        commentIds.remove(commentId);
        log.info("子评论ID列表：{}", commentIds);

        if (!commentIds.isEmpty()){
            List<CommentVO> childComments = commentMapper.selectChildComments(commentIds);
            for (CommentVO childCommentVO : childComments) {
                childCommentVO.setUserName(userMapper.selectById(childCommentVO.getUserId()).getUsername());
            }

            commentVO.setChildComments(childComments);
        }

        return commentVO;
    }

//    删除评论

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Integer> deleteCommentById(Integer commentId) {
        Queue<Integer> queue = new LinkedList<>();
        ArrayList<Integer> commentIds = new ArrayList<>();
        queue.offer(commentId);

        while (!queue.isEmpty()) {
            Integer childId = queue.poll();
            commentIds.add(childId);

            ArrayList<Integer> tempCommentIds = commentMapper.selectChildCommentId(childId);
            if (tempCommentIds != null && !tempCommentIds.isEmpty()){
                for (Integer id : tempCommentIds) {
                    queue.offer(id);
                }
            }
        }
        commentMapper.deleteCommentById(commentIds);
        log.info("删除评论完成");
        commentLikeMapper.deleteCommentLikeByCommentIds(commentIds);
        
        return commentIds;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelCommentLike(CancelCommentLikeDTO cancelCommentLikeDTO) {
        Integer commentId = cancelCommentLikeDTO.getCommentId();
        Integer userId = cancelCommentLikeDTO.getUserId();
        
        int deleted = commentLikeMapper.deleteByUserIdAndCommentId(userId, commentId);
        if (deleted > 0) {
            commentMapper.addLikeCount(commentId, -1);
        }
    }

    @Override
    public List<Integer> selectCommentIds(Integer questionId, Integer pageNum, Integer pageSize) {
        // 计算分页的起始索引
        Integer offset = (pageNum - 1) * pageSize;
        return commentMapper.selectCommentIds(questionId, offset, pageSize);
    }


}
