package oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import oj.constant.pojo.CommentLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    @Select("select comment_id from comment_likes where parent_comment_id = #{commentId}")
    ArrayList<Integer> selectChildCommentId(Integer commentId);

    @Delete("delete from comment_likes where user_id = #{userId} and comment_id = #{commentId}")
    void deleteByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);


    void deleteCommentLikeByCommentIds(@Param("commentIds") ArrayList<Integer> commentIds);
}
