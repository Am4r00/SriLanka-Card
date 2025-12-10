package com.SriLankaCard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/giftcard")
    public String giftcard() {
        return "giftcard";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/forgot")
    public String forgot() {
        return "forgot";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("/produto")
    public String produto() {
        return "produto";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/verify")
    public String verify() {
        return "verify";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    @GetMapping("/addEmploye")
    public String addEmploye() {
        return "addEmploye";
    }

    @GetMapping("/home_admin")
    public String homeAdmin() {
        return "home_admin";
    }
    
    @GetMapping("/usuariodetalhe")
    public String usuarioDetalhe() {
        return "usuariodetalhe";}

    @GetMapping("/confirmacaoPagamento")
    public String showConfirmacaoPage() {
        return "confirmacaoPagamento";
    }
}
