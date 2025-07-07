package com.educonnect.auth.service;


import com.educonnect.auth.dto.request.SendOtpRequest;
import com.educonnect.exceptionhandling.exception.BusinessRuleViolationException;
import com.educonnect.exceptionhandling.exception.EmailSenderException;
import com.educonnect.utils.EmailValidationUtil;
import com.educonnect.utils.PasswordValidationUtil;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmailService {

    JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(SendOtpRequest request){
        if (!EmailValidationUtil.isValid(request.getTo()) || request.getOtp() == null){
            throw new BusinessRuleViolationException("Give valid email credentials");
        }

        try{
//            SimpleMailMessage mail = new SimpleMailMessage();
//            mail.setTo(to);
//            mail.setSubject("Verify Your Email");
//            mail.setText("Your OTP is " + otp);
//            javaMailSender.send(mail);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

            helper.setTo(request.getTo());
            helper.setSubject("Verify Your Email");

            // HTML body
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "<h2 style='color: #333;'>Your OTP Code</h2>" +
                    "<p style='font-size: 18px;'>Use the following OTP to complete your verification:</p>" +
                    "<h1 style='font-size: 36px; color: #007BFF;'>" + request.getOtp() + "</h1>" +
                    "</div>";

            helper.setText(htmlContent, true); // true = send as HTML

            javaMailSender.send(message);

        }
        catch (Exception e){
            throw new EmailSenderException(e.getCause().toString());
        }
    }

}
