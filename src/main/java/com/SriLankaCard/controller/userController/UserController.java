package com.SriLankaCard.controller.userController;

import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.request.user.userPublic.UpdateUserRequest;
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

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("usuario", new RegisterUserRequest());
        return "signup";
    }

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
    public UserResponse getMe(Authentication auth) {
        return userServiceImple.findByEmail(auth.getName());
    }

    @PatchMapping("/me")
    @ResponseBody
    public UserResponse updateMe(@Valid @RequestBody UpdateUserRequest dto, Authentication auth) {
        String emailAtual = auth.getName();
        return userServiceImple.updateUser(emailAtual, dto);
    }
}
