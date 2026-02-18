package oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import oj.constant.dto.CancelCommentLikeDTO;
import oj.constant.dto.CommentDTO;
import oj.constant.dto.CommentLikeDTO;
import oj.constant.pojo.Comment;
import oj.constant.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    void addComment(CommentDTO commentDTO);

    void addCommentLikeDTO(CommentLikeDTO commentLikeDTO);

    CommentVO getCommentById(Integer commentId);

    void cancelCommentLike(CancelCommentLikeDTO cancelCommentLikeDTO);

    List<Integer> selectCommentIds(Integer questionId, Integer pageNum, Integer pageSize);



    void deleteCommentById(Integer commentId);
}
