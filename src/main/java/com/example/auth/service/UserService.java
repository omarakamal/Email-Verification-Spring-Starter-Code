package com.example.auth.service;

import com.example.auth.entity.AppUser;
import com.example.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JavaMailSender mailSender;


//    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @Autowired
//    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.userRepository = userRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }



    //    This method is used to define the sender, reciever, and the body of the
//    MessagingException is an exception related to email messaging
    private void sendVerificationEmail(AppUser user) throws MessagingException {
//        Mime stands for Multipurpose Internet Mail Extensions
        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        Here we are setting the properties for our emailing and
//        first argument (false) is referring to if we want to send a miltiPart email
//        second argument (utf-8) is referring to the encoding for the message
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
//        We are storing the url that will send the user to the verify endpoint with the token
        String verifyUrl = "http://localhost:8080/api/verify?token=" + user.getVerificationToken();
//        This is the body of the message which we can write using html. the important part is to include a <a> tag with the token in the href attribute
        String htmlMsg = "<h3>Verify your email address</h3><br><a href=\"" + verifyUrl + "\">Click here to verify</a> <br/> <img style='height:300px;width:300px' src=\"https://thumbs.dreamstime.com/z/guy-pointing-to-something-man-showing-her-finger-up-funny-isolated-white-background-studio-shot-68101877.jpg\">";
//      setting the content
//        first argument: message
//        second argument: html
        mimeMessage.setContent(htmlMsg, "text/html");
//        Using the getting to get the email of the user we want to email
        helper.setTo(user.getEmail());
//        Subject line of the email
        helper.setSubject("VERIFY EMAIL NOW PLEASE!!!!!");
//        Sends the email
        mailSender.send(mimeMessage);
    }

    public AppUser saveUser(AppUser user) throws MessagingException {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        generate a token
//        save token as verificationToken
        user.setVerificationToken(UUID.randomUUID().toString());


        AppUser savedUser = userRepository.save(user);

        sendVerificationEmail(savedUser);

        return savedUser;

    }

    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}