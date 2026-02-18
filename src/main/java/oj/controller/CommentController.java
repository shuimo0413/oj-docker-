package oj.controller;


import lombok.extern.slf4j.Slf4j;
import oj.constant.dto.CancelCommentLikeDTO;
import oj.constant.dto.CommentLikeDTO;
import oj.constant.pojo.Result;
import oj.constant.dto.CommentDTO;
import oj.constant.vo.CommentVO;
import oj.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/addComment")
    public Result addComment(@RequestBody CommentDTO commentDTO) throws IllegalAccessException {
//        敏感词检索系统



        log.info("新增评论：{}", commentDTO);
        commentService.addComment(commentDTO);
        return Result.success();
    }

    @PostMapping("/addCommentLike")
    public Result addCommentLikeDTO(@RequestBody CommentLikeDTO commentLikeDTO) throws IllegalAccessException {
        log.info("新增评论点赞：{}", commentLikeDTO);
        commentService.addCommentLikeDTO(commentLikeDTO);
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
        // 后续根据commentIds查询评论详情
        for (Integer commentId : commentIds) {

            CommentVO commentVO = commentService.getCommentById(commentId);
            commentVOList.add(commentVO);
        }
        return Result.success(commentVOList);
    }

//    删除评论
    @DeleteMapping("/deleteComment")
    public Result removeComment(@RequestParam Integer id){
        commentService.deleteCommentById(id);
        return Result.success();
    }


//    取消点赞
    @DeleteMapping("/cancelCommentLike")
    public Result cancelCommentLike(@RequestBody CancelCommentLikeDTO cancelCommentLikeDTO){
        commentService.cancelCommentLike(cancelCommentLikeDTO);
        return Result.success();
    }
}
