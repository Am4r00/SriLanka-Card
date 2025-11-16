package com.SriLankaCard.controller.userController;

import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.service.userServices.publicService.UserServiceImple;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImple userServiceImple;

    // EXIBE PÁGINA DE CADASTRO
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("usuario", new RegisterUserRequest());
        return "signup"; // retorna signup.html via Thymeleaf
    }

    // RECEBE FORMULÁRIO DO CADASTRO
    @PostMapping("/create-user")
    public String createUser(@ModelAttribute("usuario") @Valid RegisterUserRequest dto) {
        userServiceImple.createUser(dto);
        return "redirect:/login";
    }

    @GetMapping("/list")
    @ResponseBody
    public List<UserResponse> findAllUsers(Authentication authentication) {
        return userServiceImple.findAll();
    }

    @GetMapping("/me")
    @ResponseBody
    public UserResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userServiceImple.findByEmail(email);
    }
}
