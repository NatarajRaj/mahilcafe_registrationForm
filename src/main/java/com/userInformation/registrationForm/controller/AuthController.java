package com.userInformation.registrationForm.controller;

import com.userInformation.registrationForm.DTO.User;
import com.userInformation.registrationForm.DTO.UserDTO;
import com.userInformation.registrationForm.UserImplService;
import com.userInformation.registrationForm.repo.UserRepository;
import com.userInformation.registrationForm.repo.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserImplService userImplService;
    @Autowired
    private UserRepository userRepository;


    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public String home() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDTO user = new UserDTO();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDTO userDto,
                               BindingResult result,
                               Model model) {

        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser != null) {
            result.rejectValue("email", null, "There is already an account registered with the same email");
        }


        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String users(Model model){
        List<UserDTO> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

}