package oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import oj.pojo.dto.UpdateUserInfo;
import oj.pojo.entity.User;
import oj.pojo.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{username}")
    UserVO selectByUserName(String username);

    @Select("select password from user where username = #{username}")
    String getOldPasswordByUsername(String username);

    @Update("update user set password = #{newPassword} where username = #{username}")
    void updatePasswordByUsername(String username, String newPassword);

    @Update("update user set username = #{updateUserInfo.username} where username = #{username}")
    void updateUserInfo(String username, UpdateUserInfo updateUserInfo);
}