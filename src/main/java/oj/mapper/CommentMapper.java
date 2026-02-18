package oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import oj.constant.pojo.Comment;
import oj.constant.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
        // 根据父评论ID查询子评论
    List<CommentVO> selectChildComments(@Param("commentIds") List<Integer> commentIds);


    @Select("select id from comments where parent_comment_id = #{cid}")
    ArrayList<Integer> selectChildCommentId(Integer cid);


    @Select("update comments set like_count = like_count + #{count} where id = #{commentId}")
    void addLikeCount(@Param("commentId") Integer commentId, @Param("count") Integer count);


    @Select("select id from comments where question_id = #{questionId} and parent_comment_id is null order by create_time desc limit #{offset}, #{pageSize}")
    List<Integer> selectCommentIds(@Param("questionId") Integer questionId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);



    void deleteCommentById(@Param("commentIds") ArrayList<Integer> commentIds);

}
