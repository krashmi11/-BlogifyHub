package com.example.blog.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.Helpper.helper;
import com.example.blog.Model.Users;
import com.example.blog.Repository.userRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    userRepo ur;

    @Autowired
    public BCryptPasswordEncoder bEncoder;

    @GetMapping("/")
    public String home(Model m) {
        m.addAttribute("title", "Home-mY Blog");
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "About";
    }

    @GetMapping("/signup")
    public String register(Model m) {
        m.addAttribute("title", "Registration-mY Blog");
        m.addAttribute("user", new Users());
        return "signup";
    }

    @PostMapping("/do-register")
    public String process_registration(@ModelAttribute("user") Users user, HttpSession session,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            @RequestParam("profileImage") MultipartFile profile, Model m) {
        try {
            if (!agreement) {
                throw new Exception("You have not agreed to terms and conditions");
            }
            if (profile.isEmpty()) {
                user.setImageurl("default.png");
            }
            if (!profile.isEmpty()) {
                user.setImageurl(profile.getOriginalFilename());

                // Define the absolute path for the image storage directory
                String uploadDirectory = "D:\\SpringBoot\\blogImages";

                // Create the directory if it doesn't exist
                File uploadDir = new File(uploadDirectory);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Save the file to the specified directory
                Path path = Paths.get(uploadDirectory + File.separator + profile.getOriginalFilename());
                Files.copy(profile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded");
            }

            user.setEnabled(true);
            user.setRole("ROLE_USER");
            user.setPassword(bEncoder.encode(user.getPassword()));
            ur.save(user);
            m.addAttribute("user", new Users());
            System.out.println("Registration successful!");
            session.setAttribute("message", new helper("Successfully Registered!!", "alert-success"));
            System.out.println(session.getAttribute("message"));

        } catch (Exception e) {
            session.setAttribute("message", new helper("Something Went Wrong!!" + e.getMessage(), "alert-danger"));
            System.out.println(session.getAttribute("message"));
        }
        return "signup";
    }

    @GetMapping("/login")
    public String login(Model m) {
        m.addAttribute("title", " mY Blog-login");
        return "login";
    }
}
