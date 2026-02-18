package oj.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import oj.constant.dto.CancelCommentLikeDTO;
import oj.constant.dto.CommentDTO;
import oj.constant.dto.CommentLikeDTO;
import oj.constant.pojo.CommentLike;
import oj.constant.vo.CommentVO;
import oj.mapper.CommentLikeMapper;
import oj.mapper.CommentMapper;
import oj.constant.pojo.Comment;
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
    public void addComment(CommentDTO commentDTO){
        Comment comment = ClassMergeUtils.merge(commentDTO, new Comment());
//        根据userId获取用户名
        log.info("评论：{}", comment);

        commentMapper.insert(comment);

    }

    @Override
    public void addCommentLikeDTO(CommentLikeDTO commentLikeDTO) {
        CommentLike commentLike = ClassMergeUtils.merge(commentLikeDTO, new CommentLike());

        commentLikeMapper.insert(commentLike);
        Integer commentId = commentLike.getCommentId();
        // 更新评论的点赞数量
        commentMapper.addLikeCount(commentId,1);
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
    public void deleteCommentById(Integer commentId) {
//        根据评论内容，寻找评论的子评论
        Queue<Integer> queue = new LinkedList<>();
        ArrayList<Integer> commentIds = new ArrayList<>();
        queue.offer(commentId);

        while (!queue.isEmpty()) {
//            获取当前队列的头元素
            Integer childId = queue.poll();
            commentIds.add(childId);

            ArrayList<Integer> tempCommentIds = commentMapper.selectChildCommentId(childId);
            if (tempCommentIds != null && !tempCommentIds.isEmpty()){
                for (Integer id : tempCommentIds) {
                    queue.offer(id);
                }
            }
        }
//        我们已经找到了所有的评论的id，所以我们可以开始删除了
        commentMapper.deleteCommentById(commentIds);
        log.info("删除评论完成");
//        我们删除了所有的评论，所有的点赞也得跟着删除
        commentLikeMapper.deleteCommentLikeByCommentIds(commentIds);

    }


    @Override
    public void cancelCommentLike(CancelCommentLikeDTO cancelCommentLikeDTO) {
//        根据commentId删除评论点赞
        commentMapper.addLikeCount(cancelCommentLikeDTO.getCommentId(), -1);
        commentLikeMapper.deleteByUserIdAndCommentId(cancelCommentLikeDTO.getUserId(), cancelCommentLikeDTO.getCommentId());
    }

    @Override
    public List<Integer> selectCommentIds(Integer questionId, Integer pageNum, Integer pageSize) {
        // 计算分页的起始索引
        Integer offset = (pageNum - 1) * pageSize;
        return commentMapper.selectCommentIds(questionId, offset, pageSize);
    }


}
