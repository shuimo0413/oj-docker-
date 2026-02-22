package oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import oj.pojo.dto.CancelCommentLikeDTO;
import oj.pojo.dto.CommentDTO;
import oj.pojo.dto.CommentLikeDTO;
import oj.pojo.entity.Comment;
import oj.pojo.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    Integer addComment(CommentDTO commentDTO);

    void addCommentLikeDTO(CommentLikeDTO commentLikeDTO);

    CommentVO getCommentById(Integer commentId);

    void cancelCommentLike(CancelCommentLikeDTO cancelCommentLikeDTO);

    List<Integer> selectCommentIds(Integer questionId, Integer pageNum, Integer pageSize);



    List<Integer> deleteCommentById(Integer commentId);
}
