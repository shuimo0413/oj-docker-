package oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import oj.pojo.dto.ChangePasswordDTO;
import oj.pojo.dto.UpdateUserInfo;
import oj.pojo.dto.UserDTO;
import oj.pojo.dto.AddUserDTO;
import oj.pojo.entity.User;
import oj.pojo.vo.AddUserVO;
import oj.pojo.vo.UserVO;

public interface UserService extends IService<User> {
    UserVO login(UserDTO userDTO);

    AddUserVO addUser(AddUserDTO addUserDTO);

    String getUsernameByToken(String token);

    String changePassword(ChangePasswordDTO changePasswordDTO,String token);

    void updateUserInfo(String username, UpdateUserInfo updateUserInfo);
}
