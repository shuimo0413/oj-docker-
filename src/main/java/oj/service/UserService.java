package oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import oj.constant.dto.ChangePasswordDTO;
import oj.constant.dto.UpdateUserInfo;
import oj.constant.dto.UserDTO;
import oj.constant.dto.AddUserDTO;
import oj.constant.entity.User;
import oj.constant.vo.AddUserVO;
import oj.constant.vo.UserVO;

public interface UserService extends IService<User> {
    UserVO login(UserDTO userDTO);

    AddUserVO addUser(AddUserDTO addUserDTO);

    String getUsernameByToken(String token);

    String changePassword(ChangePasswordDTO changePasswordDTO,String token);

    void updateUserInfo(String username, UpdateUserInfo updateUserInfo);
}
