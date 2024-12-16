package com.userInformation.registrationForm;

import com.userInformation.registrationForm.DTO.Role;
import com.userInformation.registrationForm.DTO.User;
import com.userInformation.registrationForm.DTO.UserDTO;
import com.userInformation.registrationForm.repo.RoleRepository;
import com.userInformation.registrationForm.repo.UserRepository;
import com.userInformation.registrationForm.repo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserImplService implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserImplService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDTO userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setMobileNumber(userDto.getMobileNumber());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDTO mapToUserDto(User user) {
        UserDTO userDto = new UserDTO();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setMobileNumber(user.getMobileNumber());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomerNotFoundException("No account found with that email.");
        }
        user.setResetPasswordToken(token);
        userRepository.save(user);
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(User user, String newPassword, String token) {
        user.setPassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(token);
        userRepository.save(user);
    }
}

