package oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import oj.pojo.dto.ChangePasswordDTO;
import oj.pojo.dto.UpdateUserInfo;
import oj.util.MD5Utils;
import oj.mapper.UserMapper;
import oj.pojo.dto.UserDTO;
import oj.pojo.dto.AddUserDTO;
import oj.pojo.entity.User;
import oj.pojo.vo.AddUserVO;
import oj.pojo.vo.UserVO;
import oj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static oj.constant.UserConstants.USER_TOKEN_TIMEOUT;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;



    @Override
    public UserVO login(UserDTO userDTO) {
        log.info("登录请求：{}", userDTO);
//        转成md5密码
        String userPassword = MD5Utils.md5(userDTO.getPassword());
//        加密后的密码
        log.info("原始密码：{}",'['+ userDTO.getPassword()+ ']');
        log.info("加密后的密码：{}", userPassword);
        UserVO userVO = userMapper.selectByUserName(userDTO.getUsername());
//        生成token，用用户名和密码拼接


        if (userVO == null) {
            UserVO errorVO = new UserVO();
            errorVO.setMessage("用户不存在");
            return errorVO;
        }
        log.info("密码：{}", userVO.getPassword());
        log.info(userPassword);
        if (!userVO.getPassword().equals(userPassword)) {
            userVO.setMessage("密码错误");
            return userVO;
        } else if(userVO.getRoles().equals("killed")){
            userVO.setMessage("用户已被封禁");
            return userVO;
        }


        String token = MD5Utils.md5(userDTO.getUsername() + userDTO.getPassword());
        userVO.setToken(token);
//        将token存入redis
        redisTemplate.opsForValue().set(token, userVO.getUsername(), USER_TOKEN_TIMEOUT, TimeUnit.HOURS);
        userVO.setToken(token);
        userVO.setMessage("登录成功");

        return userVO;
    }

    @Override
    public AddUserVO addUser(AddUserDTO addUserDTO) {
        UserVO userVO = userMapper.selectByUserName(addUserDTO.getUsername());

        if (userVO != null) {
            AddUserVO addUserVO = new AddUserVO();
            addUserVO.setMessage("用户名已存在");
            return addUserVO;
        }

        // 转换为 User 实体类
        User user = new User();
        user.setId(addUserDTO.getId());
        user.setUsername(addUserDTO.getUsername());
        user.setPassword(MD5Utils.md5(addUserDTO.getPassword()));
        user.setRoles("user"); // 默认角色
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
//        主键返回
        userMapper.insert(user);
        log.info("添加用户成功：{}", user.getId());

        AddUserVO addUserVO = new AddUserVO();
        addUserVO.setId(Integer.parseInt(user.getId()));
        addUserVO.setUsername(user.getUsername());

        return addUserVO;
    }

    @Override
    public String getUsernameByToken(String token) {
        Object username = redisTemplate.opsForValue().get(token);
        if (username == null) {
            return "";
        }
        return (String) username;
    }

    @Override
    public String changePassword(ChangePasswordDTO changePasswordDTO, String token) {
        String username = (String) redisTemplate.opsForValue().get(token);
        if (username == null) {
            return "未登录或登录已过期";
        }
        String oldPassword1 = userMapper.getOldPasswordByUsername(username);
        if (!oldPassword1.equals(MD5Utils.md5(changePasswordDTO.getOldPassword()))) {
            return "旧密码错误";
        }else{
            String newPassword1 = MD5Utils.md5(changePasswordDTO.getNewPassword());
            userMapper.updatePasswordByUsername(username, newPassword1);
            return "密码修改成功";
        }
    }

    @Override
    public void updateUserInfo(String username, UpdateUserInfo updateUserInfo) {
        log.info("更新用户{}的信息为{}", username, updateUserInfo.getUsername());
        userMapper.updateUserInfo(username, updateUserInfo);

    }
}
