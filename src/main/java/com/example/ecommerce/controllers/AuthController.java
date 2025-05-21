package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.User;
import com.example.ecommerce.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam String role, Model model) {
        userService.registerUser(user, role);
        model.addAttribute("success", "Registration successful! You can now log in.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }
}
