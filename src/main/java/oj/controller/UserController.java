package oj.controller;


import lombok.extern.slf4j.Slf4j;
import oj.constant.pojo.Result;

import oj.constant.dto.ChangePasswordDTO;
import oj.constant.dto.UpdateUserInfo;
import oj.constant.dto.UserDTO;
import oj.constant.dto.AddUserDTO;
import oj.constant.vo.AddUserVO;
import oj.constant.vo.UserVO;
import oj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

//    登录
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        log.info("登录请求：{}", userDTO);
        UserVO userVO = userService.login(userDTO);
        if (userVO.getMessage() == "登录成功") {
            userVO.setPassword(null);
            return Result.success(userVO);
        }
        return Result.defined(1001, userVO.getMessage(), null);

    }

//    注册
    @PostMapping("/register")
    public Result addUser(@RequestBody AddUserDTO addUserDTO) {
        log.info("添加用户请求：{}", addUserDTO);

        AddUserVO addUserVO = userService.addUser(addUserDTO);
        log.info("添加用户成功：{}", addUserVO);
        if (addUserVO.getMessage() == "用户名已存在") {
            return Result.defined(0,"用户已存在",null);
        }

//        (Integer code, String msg, Object data, Object list)
        Result res = Result.defined(1, "添加用户成功", addUserVO);
        return res;
    }

//    登出,前端传token,从redis中删除token
    @PostMapping("/logout")
    public Result logout(@RequestHeader("token") String token) {
        log.info("登出请求");
//        在redis中查找token对应的用户名
        String username = userService.getUsernameByToken(token);
        log.info("token对应的用户名：{}", username);

        if (username.isEmpty()) {
            return Result.defined(1001, "token不存在", null);
        }
//        userService.logout();
        return Result.success();
    }

//    修改密码
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, @RequestHeader("Authorization") String token) {
        log.info("修改密码请求：{}", changePasswordDTO);
        if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())){
            return Result.error("新密码不能与旧密码相同");
        }
        String message = userService.changePassword(changePasswordDTO, token);
        if (message == "密码修改成功") {
            return Result.success("密码修改成功");
        }else if(message == "旧密码错误"){
            return Result.error("旧密码错误");
        }
        return Result.error(message);
    }


//    更新用户
    @PutMapping("/info")
    public Result updateUserInfo(@RequestHeader("Authorization") String token,@RequestBody UpdateUserInfo updateUserInfo){
        log.info("用户更新用户信息：{}", updateUserInfo);
        // 从token中获取用户名
        String username = userService.getUsernameByToken(token);
        if (username.isEmpty()) {
            return Result.error("token不存在");
        }
        userService.updateUserInfo(username,updateUserInfo);
        return Result.success("用户信息更新成功");
    }

    @GetMapping("/status")
    public Result getUserStatus(@RequestHeader("Authorization") String token){
        log.info("获取用户状态请求");
        // 从token中获取用户名
        String username = userService.getUsernameByToken(token);
        if (username.isEmpty()) {
            return Result.error("token不存在");
        }
        return Result.success(username);
    }
    
}
