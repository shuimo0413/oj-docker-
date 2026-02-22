package oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import oj.pojo.entity.CommentLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    @Select("select comment_id from comment_likes where parent_comment_id = #{commentId}")
    ArrayList<Integer> selectChildCommentId(Integer commentId);

    @Delete("delete from comment_likes where user_id = #{userId} and comment_id = #{commentId}")
    int deleteByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Select("select count(*) from comment_likes where user_id = #{userId} and comment_id = #{commentId}")
    int countByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    void deleteCommentLikeByCommentIds(@Param("commentIds") ArrayList<Integer> commentIds);
}
