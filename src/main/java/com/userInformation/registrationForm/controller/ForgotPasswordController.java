package com.userInformation.registrationForm.controller;

import com.userInformation.registrationForm.CustomerNotFoundException;
import com.userInformation.registrationForm.DTO.User;
import com.userInformation.registrationForm.UserImplService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserImplService userImplService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userImplService.updateResetPasswordToken(token, email);

            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (CustomerNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "redirect:/forgot_password?success=true";
    }

    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("anataraj95@gmail.com", "Application Support");
//        helper.setTo(recipientEmail);
        helper.setTo("anataraj95@gmail.com"); // to forgot password reset admin users only, instead of sent receipent mail

        String subject = "Here's the link to reset your password";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {

        User user = userImplService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "reset_password";
        }
        return "reset_password";
    }


    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = userImplService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "/reset_password";
        } else {
            userImplService.updatePassword(user, password, token);

            model.addAttribute("message", "You have successfully changed your password.");
        }

        return "/reset_password";
    }
}

