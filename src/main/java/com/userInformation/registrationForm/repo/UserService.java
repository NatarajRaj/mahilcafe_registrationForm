package com.userInformation.registrationForm.repo;


import com.userInformation.registrationForm.DTO.User;
import com.userInformation.registrationForm.DTO.UserDTO;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDto);

    User findUserByEmail(String email);

    List<UserDTO> findAllUsers();
}
