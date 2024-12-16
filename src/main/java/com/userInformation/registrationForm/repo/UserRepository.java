package com.userInformation.registrationForm.repo;

import com.userInformation.registrationForm.DTO.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String  email);
    public User findByResetPasswordToken(String token);

}
